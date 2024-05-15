package com.ecommerce.apigateway.service;

import com.ecommerce.apigateway.collection.Offer;
import com.ecommerce.apigateway.collection.Product;
import com.ecommerce.apigateway.collection.request.OfferRequest;
import com.ecommerce.apigateway.collection.request.ResponseTemplateVO;
import com.fasterxml.jackson.core.type.TypeReference;
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
    private static final String GRAPHQL_URL = "http://offer-graphql:8182/graphql";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public Product addProductOffer(OfferRequest offerRequest) {
        String mutation = String.format(
                "mutation { addOffer(offerRequest: { id: %s, productId: %s, discountOffer: %f }) { productTitle, imageUrl, discountOffer, price }}",
                offerRequest.getId(),
                offerRequest.getProductId(),
                offerRequest.getDiscountOffer()
        );

        return executeGraphQLMutation(mutation, "addOffer", Product.class);
    }

    public List<Offer> getOffers() {
        String query = "query { allOffers { id, productId, discountOffer }}";
        return executeGraphQLQuery(query, "allOffers", new TypeReference<List<Offer>>() {});
    }

    public ResponseTemplateVO getOfferWithProduct(Integer id) {
        String query = String.format("query { offerWithProduct(id: %d) { product { productTitle, imageUrl, discountOffer, price, currentPrice }}}", id);
        return executeGraphQLQuery(query, "offerWithProduct", ResponseTemplateVO.class);
    }

    private String performHttpPostRequest(String requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(String.format("{\"query\": \"%s\"}", requestBody), headers);

        ResponseEntity<String> response = restTemplate.exchange(GRAPHQL_URL, HttpMethod.POST, request, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("HTTP request failed with status code: {}", response.getStatusCode());
            return null;
        }
        return response.getBody();
    }

    private <T> T executeGraphQLQuery(String query, String rootField, Class<T> responseType) {
        String response = performHttpPostRequest(query);
        return parseGraphQLResponse(response, rootField, responseType);
    }

    private <T> T executeGraphQLQuery(String query, String rootField, TypeReference<T> responseType) {
        String response = performHttpPostRequest(query);
        return parseGraphQLResponse(response, rootField, responseType);
    }

    private <T> T executeGraphQLMutation(String mutation, String rootField, Class<T> responseType) {
        String response = performHttpPostRequest(mutation);
        return parseGraphQLResponse(response, rootField, responseType);
    }

    private <T> T parseGraphQLResponse(String response, String rootField, Class<T> responseType) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode dataNode = rootNode.get("data").get(rootField);
            return objectMapper.treeToValue(dataNode, responseType);
        } catch (Exception e) {
            log.error("Error parsing GraphQL response: {}", e.getMessage());
            return null;
        }
    }

    private <T> T parseGraphQLResponse(String response, String rootField, TypeReference<T> responseType) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode dataNode = rootNode.get("data").get(rootField);
            return objectMapper.readValue(dataNode.toString(), responseType);
        } catch (Exception e) {
            log.error("Error parsing GraphQL response: {}", e.getMessage());
            return null;
        }
    }
}
