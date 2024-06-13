package com.ecommerce.product.controller;

import com.ecommerce.product.collection.Product;
import com.ecommerce.product.collection.request.ProductRequest;
import com.ecommerce.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product-rest")
@Slf4j
@CrossOrigin(origins="http://localhost:3000", allowedHeaders="*")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/product")
    public Product saveProduct(@RequestBody ProductRequest productRequest) {
        return productService.saveProduct(productRequest);
    }

    @PostMapping("/addOffer")
    public Product saveOffer(@RequestBody Product product) {
        return productService.saveOffer(product);
    }

    @PutMapping("/updatePrice")
    public Product addPrice(@RequestParam Integer id, @RequestParam Double price){
        return productService.updatePrice(id, price);
    }

    @GetMapping("/product")
    public List<Product> getAllProducts(){
        return productService.getAllProducts();
    }

    @GetMapping("/product/{id}")
    public Product getProduct(@PathVariable Integer id){
        return productService.getProductById(id).isPresent() ? productService.getProductById(id).get() : null;
    }
}
