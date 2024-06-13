package com.devproblems.grpc.server.controller;

import com.devProblems.AddPriceRequest;
import com.devProblems.OfferRequest;
import com.devproblems.grpc.server.service.OfferClientService;
import com.devproblems.grpc.server.service.ProductClientService;
import com.google.protobuf.Descriptors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/product-grpc")
@Slf4j
public class ProductController {

    final ProductClientService productClientService;

    @GetMapping("/product/{id}")
    public Map<Descriptors.FieldDescriptor, Object> getProduct(@PathVariable String id) {
        return productClientService.getProductById(Integer.parseInt(id));
    }

    @GetMapping("/products")
    public List<Map<Descriptors.FieldDescriptor, Object>> getAllProducts() throws InterruptedException {
        return productClientService.getAllProducts();
    }

    @PostMapping("/addOffer")
    public Map<Descriptors.FieldDescriptor, Object> saveOffer(@RequestBody com.devproblems.grpc.server.collection.Product product) {
        com.devProblems.Product productRequest = com.devProblems.Product.
                newBuilder().
                setId(product.getId()).
                setProductCode(product.getProductCode()).
                setProductTitle(product.getProductTitle()).
                setImageUrl(product.getImageUrl()).
                setDiscountOffer(product.getDiscountOffer()).
                setPrice(product.getPrice()).
                setCurrentPrice(product.getCurrentPrice()).build();

        return productClientService.saveOffer(productRequest);
    }

    @PostMapping("/product")
    public Map<Descriptors.FieldDescriptor, Object> saveProduct(@RequestBody com.devproblems.grpc.server.collection.Product product) {

        com.devProblems.ProductRequest productRequest = com.devProblems.ProductRequest.
                newBuilder().
                setId(product.getId()).
                setProductCode(product.getProductCode()).
                setProductTitle(product.getProductTitle()).
                setImageUrl(product.getImageUrl()).
                setDiscountOffer(product.getDiscountOffer()).
                setPrice(product.getPrice()).build();


        return productClientService.saveProduct(productRequest);
    }

    @PutMapping("/addPrice")
    public Map<Descriptors.FieldDescriptor, Object> addPrice(@RequestParam Integer id, @RequestParam Double price) {
        AddPriceRequest addPriceRequest = AddPriceRequest.newBuilder().
                setId(id).setPrice(price).
        build();
        return productClientService.addPrice(addPriceRequest);
    }

}