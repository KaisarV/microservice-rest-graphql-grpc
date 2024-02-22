package com.devproblems.grpc.server.repository;

import com.devProblems.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductProtobufRepository extends MongoRepository<Product, Integer> {
    List<Product> findAllByOrderByPriceAsc();

}
