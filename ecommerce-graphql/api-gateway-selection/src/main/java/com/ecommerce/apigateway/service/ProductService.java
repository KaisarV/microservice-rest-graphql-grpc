package com.ecommerce.apigateway.service;

import com.ecommerce.apigateway.collection.Product;
import com.ecommerce.apigateway.collection.request.ProductRequest;
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
public class ProductService {

    private static final String GRAPHQL_URL = "http://product-graphql:8181/graphql";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public Product saveProduct(ProductRequest productRequest) {
        String mutation = String.format(
                "mutation { addProduct(productRequest: { id: %s, productCode: \\\"%s\\\", productTitle: \\\"%s\\\", imageUrl: \\\"%s\\\", discountOffer: %f, price: %f }) { productTitle, imageUrl, price }}",
                productRequest.getId(),
                productRequest.getProductCode(),
                productRequest.getProductTitle(),
                productRequest.getImageUrl(),
                productRequest.getDiscountOffer(),
                productRequest.getPrice()
        );

        return executeGraphQLMutation(mutation, "addProduct", Product.class);
    }

    public List<Product> getAllProducts() {
        String query = "query { allProducts { productTitle, imageUrl, discountOffer, currentPrice }}";

        return executeGraphQLQuery(query, "allProducts", new TypeReference<List<Product>>() {});
    }

    public Product getProductById(Integer productId) {
        String query = String.format("query { productById(id: %d) { id, productTitle, discountOffer, price, currentPrice }}", productId);

        return executeGraphQLQuery(query, "productById", Product.class);
    }

    public Product updatePrice(Integer productId, Double price) {
        String mutation = String.format(
                "mutation { addPrice(addPriceRequest: { id: %d, price: %f }) { productTitle, discountOffer, price, currentPrice }}",
                productId, price
        );

        return executeGraphQLMutation(mutation, "addPrice", Product.class);
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
