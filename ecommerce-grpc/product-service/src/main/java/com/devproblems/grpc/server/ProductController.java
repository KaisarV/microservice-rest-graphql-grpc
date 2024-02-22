package com.devproblems.grpc.server;

import com.devProblems.AddPriceRequest;
import com.devProblems.Product;
import com.devProblems.ProductRequest;
import com.google.protobuf.Descriptors;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
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
    public Map<Descriptors.FieldDescriptor, Object> saveOffer(@RequestBody Product product) {
        return productClientService.saveOffer(product);
    }

    @PostMapping("/product")
    public Map<Descriptors.FieldDescriptor, Object> saveProduct(@RequestBody ProductRequest productRequest) {
        return productClientService.saveProduct(productRequest);
    }

    @PutMapping("/addPrice")
    public Map<Descriptors.FieldDescriptor, Object> addPrice(@RequestParam AddPriceRequest addPriceRequest) {
        return productClientService.addPrice(addPriceRequest);
    }
}