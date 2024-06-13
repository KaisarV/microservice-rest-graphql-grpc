package com.devproblems.grpc.server.repository;



import com.devproblems.grpc.server.collection.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, Integer> {
    List<Product> findAllByOrderByPriceAsc();

}
