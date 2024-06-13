package com.devproblems.grpc.server.service;


import com.devProblems.*;
import com.google.protobuf.Descriptors;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@Service
public class ProductClientService {

    @GrpcClient("grpc-product-service")
    ProductServiceGrpc.ProductServiceBlockingStub synchronousClient;

    @GrpcClient("grpc-product-service")
    ProductServiceGrpc.ProductServiceStub asynchronousClient;

    public Map<Descriptors.FieldDescriptor, Object> getProductById(int productId) {
        Product productRequest = Product.newBuilder().setId(productId).build();
        Product productResponse = synchronousClient.getProductById(productRequest);
        return productResponse.getAllFields();
    }



    public List<Map<Descriptors.FieldDescriptor, Object>> getAllProducts() {
        ProductsResponse productsResponse = synchronousClient.getAllProducts(Empty.newBuilder().build());
        List<Map<Descriptors.FieldDescriptor, Object>> response = new ArrayList<>();

        for (Product product : productsResponse.getProductsList()) {
            response.add(product.getAllFields());
        }

        return response;
    }



    public Map<Descriptors.FieldDescriptor, Object> saveProduct(ProductRequest productRequest) {
        Product productResponse = synchronousClient.saveProduct(productRequest);
        return productResponse.getAllFields();
    }

    public Map<Descriptors.FieldDescriptor, Object> saveOffer(Product productRequest) {
        Product productResponse = synchronousClient.saveOffer(productRequest);
        return productResponse.getAllFields();
    }

    public Map<Descriptors.FieldDescriptor, Object> addPrice(AddPriceRequest addPriceRequest) {
        Product productResponse = synchronousClient.addPrice(addPriceRequest);
        return productResponse.getAllFields();
    }
}