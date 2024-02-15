package com.ecommerce.offer.service;

import com.ecommerce.offer.VO.Product;
import com.ecommerce.offer.VO.ResponseTemplateVO;
import com.ecommerce.offer.collection.Offer;
import com.ecommerce.offer.collection.request.OfferRequest;
import com.ecommerce.offer.repository.OfferRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
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

    public void addProductOffer(OfferRequest offerRequest) {
        Optional<Offer> offer = offerRepository.findByProductId(offerRequest.getProductId());
        ResponseTemplateVO vo = new ResponseTemplateVO();

        if(offer.isPresent()){
            offer.get().setDiscountOffer(offerRequest.getDiscountOffer());
        }else {
            offer = Optional.ofNullable(new Offer().builder()
                    .id(offerRequest.getId())
                    .productId(offerRequest.getProductId())
                    .discountOffer(offerRequest.getDiscountOffer())
                    .build());
        }

        offerRepository.save(offer.get());

        // Call GraphQL API
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        // Define the GraphQL query
        String graphqlQuery = "{\"query\":\"{ productById(id: " + offerRequest.getProductId() + ") { productTitle, imageUrl, discountOffer, price, currentPrice } }\"}";

        // Create the HTTP entity with headers and query
        HttpEntity<String> entity = new HttpEntity<>(graphqlQuery, headers);

        // Make the GraphQL API call
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:8081/graphql", HttpMethod.POST, entity, String.class);

        // Parse the response and extract product data
        JSONObject responseBody = new JSONObject(response.getBody());
        JSONObject productData = responseBody.getJSONObject("data").getJSONObject("productById");

        // Create a Product object from the response
        Product product = new Product();
        product.setProductTitle(productData.getString("productTitle"));
        product.setImageUrl(productData.getString("imageUrl"));
        product.setDiscountOffer(productData.getDouble("discountOffer"));
        product.setPrice(productData.getDouble("price"));
        product.setCurrentPrice(productData.getDouble("currentPrice"));

        Double discountPrice = (offerRequest.getDiscountOffer()*product.getPrice())/100;
        product.setCurrentPrice(product.getPrice()-discountPrice);
        product.setDiscountOffer(offerRequest.getDiscountOffer());

        Product product2 = restTemplate.postForObject("http://product-rest:8081/product-rest/addOffer", product, Product.class);

        System.out.println(product2);
    }

    public List<Offer> getOffers() {
        return offerRepository.findAll();
    }

    public ResponseTemplateVO getOfferWithProduct(Integer id) {
        ResponseTemplateVO vo = new ResponseTemplateVO();
        Offer offer = offerRepository.findByid(id);

        Product product = restTemplate.getForObject("http://localhost:8081/product-rest/product/" + offer.getProductId(),Product.class);

        vo.setOffer(offer);
        vo.setProduct(product);

        return vo;
    }
}
