package com.ecommerce.product.controller;

import com.ecommerce.product.collection.Product;
import com.ecommerce.product.collection.request.AddOfferRequest;
import com.ecommerce.product.collection.request.AddPriceRequest;
import com.ecommerce.product.collection.request.ProductRequest;
import com.ecommerce.product.service.ProductService;
import com.ecommerce.product.service.SequenceGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private SequenceGeneratorService service;

    private final HttpGraphQlClient httpGraphQlClient;

    public ProductController(HttpGraphQlClient httpGraphQlClient) {
        this.httpGraphQlClient = httpGraphQlClient;
    }

    @MutationMapping
    public Product addProduct(@Argument ProductRequest productRequest) {
        productRequest.setId(service.getSequenceNumber(Product.SEQUENCE_NAME));
        productRequest.setDiscountOffer(0.0);
        return productService.saveProduct(productRequest);
    }

    //Only call by OfferAPI
    @MutationMapping
    public Product addOffer(@Argument Product product) {
        return productService.saveOffer(product);
    }

    @MutationMapping
    public Product addPrice(@Argument AddPriceRequest addPriceRequest){
        return productService.addPrice(addPriceRequest);
    }
    @QueryMapping
    public List<Product> allProducts(){
        return productService.getAllProducts();
    }

    @QueryMapping
    public Product productById(@Argument Integer id){
        return productService.getProductById(id).isPresent() ? productService.getProductById(id).get() : null;
    }
}
