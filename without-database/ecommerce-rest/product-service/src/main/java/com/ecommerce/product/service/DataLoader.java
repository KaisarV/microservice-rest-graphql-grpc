package com.ecommerce.product.service;

import com.ecommerce.product.collection.Product;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final ProductService productService;

    public DataLoader(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void run(String... args) throws Exception {
        for (int i = 1; i <= 500; i++) {
            Product product = new Product(
                    i,
                    "Kode",
                    "Tes",
                    "Tes",
                    20.0,
                    2000.0,
                    1600.0
            );
            productService.addProduct(product);
        }
    }
}
