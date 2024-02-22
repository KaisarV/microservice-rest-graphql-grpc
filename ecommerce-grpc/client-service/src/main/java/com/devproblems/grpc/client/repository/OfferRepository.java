package com.devproblems.grpc.client.repository;

import com.ecommerce.offer.collection.Offer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfferRepository extends MongoRepository<Offer, Integer> {
    Offer findByid(Integer id);

    Optional<Offer> findByProductId(Integer productId);
}
