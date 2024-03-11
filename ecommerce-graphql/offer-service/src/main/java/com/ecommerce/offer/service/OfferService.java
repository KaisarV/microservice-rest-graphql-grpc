package com.ecommerce.offer.service;

import com.ecommerce.offer.VO.Product;
import com.ecommerce.offer.VO.ResponseTemplateVO;
import com.ecommerce.offer.collection.Offer;
import com.ecommerce.offer.collection.request.OfferRequest;
import com.ecommerce.offer.repository.OfferRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.client.HttpGraphQlClient;
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

    private final HttpGraphQlClient httpGraphQlClient;

    public OfferService(HttpGraphQlClient httpGraphQlClient) {
        this.httpGraphQlClient = httpGraphQlClient;
    }

    public Product addProductOffer(OfferRequest offerRequest) {
        Optional<Offer> offer = offerRepository.findByProductId(offerRequest.getProductId());
        ResponseTemplateVO vo = new ResponseTemplateVO();

        if(offer.isPresent()){
            offer.get().setDiscountOffer(offerRequest.getDiscountOffer());
        } else {
            offer = Optional.ofNullable(new Offer().builder()
                    .id(offerRequest.getId())
                    .productId(offerRequest.getProductId())
                    .discountOffer(offerRequest.getDiscountOffer())
                    .build());
        }

        offerRepository.save(offer.get());


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String graphqlUrl = "http://localhost:8181/graphql";

        String graphqlRequestProductById = "{ \"query\": \"query { productById(id: " + offerRequest.getProductId() + ") { productCode, productTitle, imageUrl, price}}\" }";

        HttpEntity<String> requestProductById = new HttpEntity<>(graphqlRequestProductById, headers);

        String response = restTemplate.exchange(graphqlUrl, HttpMethod.POST, requestProductById, String.class).getBody();
        ResponseEntity<String> responseEntity = restTemplate.exchange(graphqlUrl, HttpMethod.POST, requestProductById, String.class);


        try {
            // Inisialisasi ObjectMapper dari Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            // Membaca JSON string ke dalam JsonNode
            JsonNode rootNode = objectMapper.readTree(response);
            // Mengambil objek langsung dari properti 'productById'
            JsonNode productNode = rootNode.get("data").get("productById");
            Product product = objectMapper.treeToValue(productNode, Product.class);

            Double discountPrice = (offerRequest.getDiscountOffer()*product.getPrice())/100;
            product.setCurrentPrice(product.getPrice()-discountPrice);
            product.setDiscountOffer(offerRequest.getDiscountOffer());
            product.setId(offerRequest.getProductId());

            String saveOfferRequestBody = String.format(
                    "{\"query\": \"mutation { " +
                            "addOffer(product : { " +
                            "id : %s, " +
                            "productCode : \\\"%s\\\", " +
                            "productTitle : \\\"%s\\\", " +
                            "imageUrl : \\\"%s\\\", " +
                            "discountOffer : %f, " +
                            "currentPrice : %f, " +
                            "price : %f " +
                            "}) { " +
                            "id, productCode, productTitle, imageUrl, discountOffer, price, currentPrice" +
                            "}}\"}",
                    product.getId(),
                    product.getProductCode(),
                    product.getProductTitle(),
                    product.getImageUrl(),
                    product.getDiscountOffer(),
                    product.getCurrentPrice(),
                    product.getPrice()
            );
            HttpEntity<String> requestSaveOffer = new HttpEntity<>(saveOfferRequestBody, headers);

            String response2 = restTemplate.exchange(graphqlUrl, HttpMethod.POST, requestSaveOffer, String.class).getBody();
            ResponseEntity<String> responseEntity2 = restTemplate.exchange(graphqlUrl, HttpMethod.POST, requestSaveOffer, String.class);
            try {
                rootNode = objectMapper.readTree(response2);
                productNode = rootNode.get("data").get("addOffer");
                product = objectMapper.treeToValue(productNode, Product.class);

                System.out.println(product.getId());
                System.out.println(responseEntity2);
                return product;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }

    public List<Offer> getOffers() {
        return offerRepository.findAll();
    }

    public ResponseTemplateVO getOfferWithProduct(Integer id) {
        ResponseTemplateVO vo = new ResponseTemplateVO();
        Offer offer = offerRepository.findByid(id);

        String graphqlRequestBody = "{ \"query\": \"query { productById(id: " + offer.getProductId() + ") {id, productCode, productTitle, imageUrl, discountOffer, price, currentPrice}}\" }";

        // Buat header untuk request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Buat entity untuk request
        HttpEntity<String> requestEntity = new HttpEntity<>(graphqlRequestBody, headers);

        // Panggil API GraphQL
        String graphqlUrl = "http://localhost:8181/graphql"; // Sesuaikan dengan URL GraphQL Anda
        ResponseEntity<String> responseEntity = restTemplate.exchange(graphqlUrl, HttpMethod.POST, requestEntity, String.class);
        String response = responseEntity.getBody();

        // Parse respons GraphQL dan setel objek Product dalam ResponseTemplateVO
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode productNode = jsonNode.get("data").get("productById");
            Product product = objectMapper.treeToValue(productNode, Product.class);
            vo.setProduct(product);
        } catch (JsonProcessingException e) {
            // Handle error saat parsing JSON
            e.printStackTrace();
        }

        vo.setOffer(offer);

        return vo;
    }

}
