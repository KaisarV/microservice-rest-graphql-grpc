package com.ecommerce.apigateway.service;

import com.ecommerce.apigateway.collection.Offer;
import com.ecommerce.apigateway.collection.Product;
import com.ecommerce.apigateway.collection.request.OfferRequest;
import com.ecommerce.apigateway.collection.request.ResponseTemplateVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class OfferService {
    @Autowired
    private RestTemplate restTemplate;

    public Product addProductOffer(OfferRequest offerRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String graphqlUrl = "http://localhost:8182/graphql";

        String addOfferRequestBody = String.format(
                "{\"query\": \"mutation { " +
                        "addOffer(offerRequest : { " +
                        "id : %s, " +
                        "productId : %s, " +
                        "discountOffer : %f, " +
                        "}) { " +
                        "id, productCode, productTitle, imageUrl, price, discountOffer, currentPrice" +
                        "}}\"}",
                offerRequest.getId(),
                offerRequest.getProductId(),
                offerRequest.getDiscountOffer()
        );

        HttpEntity<String> addProductRequest = new HttpEntity<>(addOfferRequestBody, headers);
        String addProductResponse = restTemplate.exchange(graphqlUrl, HttpMethod.POST, addProductRequest, String.class).getBody();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(addProductResponse);
            JsonNode offerNode = rootNode.get("data").get("addOffer");
            Product product = objectMapper.treeToValue(offerNode, Product.class);

            return product;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Offer> getOffers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String graphqlUrl = "http://localhost:8182/graphql";

        String allOfferRequestBody = "{ \"query\": \"query { allOffers { id, productId, discountOffer}}\" }";

        HttpEntity<String> allOfferRequest = new HttpEntity<>(allOfferRequestBody, headers);
        String allOfferResponse = restTemplate.exchange(graphqlUrl, HttpMethod.POST, allOfferRequest, String.class).getBody();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(allOfferResponse);
            JsonNode offerNode = rootNode.get("data").get("allOffers");
            List<Offer> offers = objectMapper.treeToValue(offerNode, List.class);

            return offers;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public ResponseTemplateVO getOfferWithProduct(Integer id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String graphqlUrl = "http://localhost:8182/graphql";

        String offerWithProductRequestBody = "{ \"query\": \"query { offerWithProduct(id: " + id + ") { offer {discountOffer}, product{productTitle, price, currentPrice}}}\" }";

        HttpEntity<String> offerWithProductRequest = new HttpEntity<>(offerWithProductRequestBody, headers);
        String offerWithProductResponse = restTemplate.exchange(graphqlUrl, HttpMethod.POST, offerWithProductRequest, String.class).getBody();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(offerWithProductResponse);
            JsonNode offerWithProductNode = rootNode.get("data").get("offerWithProduct");
            ResponseTemplateVO responseTemplateVO = objectMapper.treeToValue(offerWithProductNode, ResponseTemplateVO.class);

            return responseTemplateVO;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
