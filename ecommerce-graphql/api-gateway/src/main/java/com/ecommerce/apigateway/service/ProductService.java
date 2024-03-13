package com.ecommerce.apigateway.service;


import com.ecommerce.apigateway.collection.Product;
import com.ecommerce.apigateway.collection.request.ProductRequest;
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

    @Autowired
    private RestTemplate restTemplate;
    public Product saveProduct(ProductRequest productRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String graphqlUrl = "http://localhost:8181/graphql";

        String addProductRequestBody = String.format(
                "{\"query\": \"mutation { " +
                        "addProduct(productRequest : { " +
                        "id : %s, " +
                        "productCode : \\\"%s\\\", " +
                        "productTitle : \\\"%s\\\", " +
                        "imageUrl : \\\"%s\\\", " +
                        "discountOffer : %f, " +
                        "price : %f " +
                        "}) { " +
                        "id, productCode, productTitle, imageUrl, discountOffer, price, currentPrice" +
                        "}}\"}",
                productRequest.getId(),
                productRequest.getProductCode(),
                productRequest.getProductTitle(),
                productRequest.getImageUrl(),
                productRequest.getDiscountOffer(),
                productRequest.getPrice()
        );

        HttpEntity<String> addProductRequest = new HttpEntity<>(addProductRequestBody, headers);
        String addProductResponse = restTemplate.exchange(graphqlUrl, HttpMethod.POST, addProductRequest, String.class).getBody();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(addProductResponse);
            JsonNode productNode = rootNode.get("data").get("addProduct");
            Product product = objectMapper.treeToValue(productNode, Product.class);

            return product;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Product> getAllProducts() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String graphqlUrl = "http://localhost:8181/graphql";

        String allProductRequestBody = "{ \"query\": \"query { allProducts { productCode, productTitle, imageUrl, price}}\" }";

        HttpEntity<String> allProductRequest = new HttpEntity<>(allProductRequestBody, headers);
        String allProductResponse = restTemplate.exchange(graphqlUrl, HttpMethod.POST, allProductRequest, String.class).getBody();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(allProductResponse);
            JsonNode productNode = rootNode.get("data").get("allProducts");
            List<Product> products = objectMapper.treeToValue(productNode, List.class);

            return products;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Product getProductById(Integer productId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String graphqlUrl = "http://localhost:8181/graphql";

        String productByIdRequestBody = "{ \"query\": \"query { productById(id: " + productId + ") { productCode, productTitle, imageUrl, price}}\" }";

        HttpEntity<String> productByIdRequest = new HttpEntity<>(productByIdRequestBody, headers);
        String productByIdResponse = restTemplate.exchange(graphqlUrl, HttpMethod.POST, productByIdRequest, String.class).getBody();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(productByIdResponse);
            JsonNode productNode = rootNode.get("data").get("productById");
            Product product = objectMapper.treeToValue(productNode, Product.class);

            return product;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Product updatePrice(Integer productId, Double price) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String graphqlUrl = "http://localhost:8181/graphql";

        String addPriceRequestBody = "{ \"query\": \"mutation { addPrice(id: " + productId + ", price : " + price + ") { productCode, productTitle, imageUrl, price}}\" }";

        HttpEntity<String> addPriceRequest = new HttpEntity<>(addPriceRequestBody, headers);
        String addPriceResponse = restTemplate.exchange(graphqlUrl, HttpMethod.POST, addPriceRequest, String.class).getBody();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(addPriceResponse);
            JsonNode productNode = rootNode.get("data").get("addPrice");
            Product product = objectMapper.treeToValue(productNode, Product.class);

            return product;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
