package com.ecommerce.apigateway.controller;

import com.ecommerce.apigateway.collection.Product;
import com.ecommerce.apigateway.collection.request.ProductRequest;
import com.ecommerce.apigateway.service.ProductService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product-graphql")
@Slf4j
@CrossOrigin(origins="http://localhost:3000", allowedHeaders="*")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/products")
    public Product saveProduct(@RequestBody ProductRequest productRequest) {
        return productService.saveProduct(productRequest);
    }

//    //Only called by OfferService
//    @PostMapping("/addOffer")
//    public Product saveOffer(@RequestBody Product product) {
//        return productService.saveOffer(product);
//    }
//
    @PutMapping("/updatePrice")
    public Product updatePrice(@RequestParam Integer id, @RequestParam Double price){
        return productService.updatePrice(id, price);
    }

    @GetMapping("/products")
    public List<Product> getAllProducts(){
        return productService.getAllProducts();
    }

    @GetMapping("/product/{id}")
    public Product getProduct(@PathVariable Integer id){
        return productService.getProductById(id);
    }
}
