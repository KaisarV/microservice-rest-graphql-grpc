package com.ecommerce.offer.service;

import com.ecommerce.offer.VO.Product;
import com.ecommerce.offer.VO.ResponseTemplateVO;
import com.ecommerce.offer.collection.Offer;
import com.ecommerce.offer.collection.request.OfferRequest;
import com.ecommerce.offer.repository.OfferRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OfferService {
    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SequenceGeneratorService service;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String GRAPHQL_URL = "http://product-graphql:8181/graphql";

    public Product addProductOffer(OfferRequest offerRequest) {
        Offer offer = offerRepository.findByProductId(offerRequest.getProductId())
                .orElseGet(() -> new Offer(service.getSequenceNumber(Offer.SEQUENCE_NAME),
                        offerRequest.getProductId(), offerRequest.getDiscountOffer()));

        offer.setDiscountOffer(offerRequest.getDiscountOffer());
        offerRepository.save(offer);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String productQuery = String.format("{\"query\": \"query { productById(id: %d) { productCode, productTitle, imageUrl, price}}\"}", offerRequest.getProductId());
        HttpEntity<String> productRequest = new HttpEntity<>(productQuery, headers);

        try {
            ResponseEntity<String> productResponse = restTemplate.exchange(GRAPHQL_URL, HttpMethod.POST, productRequest, String.class);

            if (productResponse.getStatusCode() == HttpStatus.OK) {
                JsonNode productNode = objectMapper.readTree(productResponse.getBody()).path("data").path("productById");
                Product product = objectMapper.treeToValue(productNode, Product.class);

                double discountPrice = (offerRequest.getDiscountOffer() * product.getPrice()) / 100;
                product.setCurrentPrice(product.getPrice() - discountPrice);
                product.setDiscountOffer(offerRequest.getDiscountOffer());
                product.setId(offerRequest.getProductId());

                String addOfferMutation = String.format(
                        "{\"query\": \"mutation { addOffer(product: { id: %d, productCode: \\\"%s\\\", productTitle: \\\"%s\\\", imageUrl: \\\"%s\\\", discountOffer: %f, currentPrice: %f, price: %f }) { id, productCode, productTitle, imageUrl, discountOffer, price, currentPrice }}\"}",
                        product.getId(), product.getProductCode(), product.getProductTitle(), product.getImageUrl(), product.getDiscountOffer(), product.getCurrentPrice(), product.getPrice());
                HttpEntity<String> saveOfferRequest = new HttpEntity<>(addOfferMutation, headers);

                ResponseEntity<String> saveOfferResponse = restTemplate.exchange(GRAPHQL_URL, HttpMethod.POST, saveOfferRequest, String.class);

                if (saveOfferResponse.getStatusCode() == HttpStatus.OK) {
                    JsonNode updatedProductNode = objectMapper.readTree(saveOfferResponse.getBody()).path("data").path("addOffer");
                    return objectMapper.treeToValue(updatedProductNode, Product.class);
                } else {
                    log.error("Failed to save offer. Status code: {}", saveOfferResponse.getStatusCode());
                }
            } else {
                log.error("Failed to fetch product. Status code: {}", productResponse.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error processing offer: {}", e.getMessage());
        }
        return null;
    }

    public List<Offer> getOffers() {
        return offerRepository.findAll();
    }

    public ResponseTemplateVO getOfferWithProduct(Integer id) {
        ResponseTemplateVO vo = new ResponseTemplateVO();
        Offer offer = offerRepository.findById(id).orElse(null);

        if (offer == null) {
            log.error("Offer not found for id: {}", id);
            return vo;
        }

        vo.setOffer(offer);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String productQuery = String.format("{\"query\": \"query { productById(id: %d) { id, productCode, productTitle, imageUrl, discountOffer, price, currentPrice }}\"}", offer.getProductId());
        HttpEntity<String> request = new HttpEntity<>(productQuery, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(GRAPHQL_URL, HttpMethod.POST, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode productNode = objectMapper.readTree(response.getBody()).path("data").path("productById");
                Product product = objectMapper.treeToValue(productNode, Product.class);
                vo.setProduct(product);
            } else {
                log.error("Failed to fetch product. Status code: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error fetching product for offer: {}", e.getMessage());
        }

        return vo;
    }
}
