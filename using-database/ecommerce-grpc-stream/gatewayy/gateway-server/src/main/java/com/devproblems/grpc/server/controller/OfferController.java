package com.devproblems.grpc.server.controller;


import com.devproblems.grpc.server.collection.Offer;
import com.devproblems.grpc.server.service.OfferClientService;
import com.google.protobuf.Descriptors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/offer-grpc")
@Slf4j
public class OfferController {

    final OfferClientService offerClientService;

    @PostMapping("/offer")
    public Map<Descriptors.FieldDescriptor, Object> addProductOffer(@RequestBody Offer offerRequest) {

        com.devProblems.OfferRequest offerRequest1 = com.devProblems.OfferRequest.
                newBuilder().
                setId(offerRequest.getId()).
                setProductId(offerRequest.getProductId()).
                setDiscountOffer(offerRequest.getDiscountOffer()).build();

        return offerClientService.addProductOffer(offerRequest1);
    }

    @GetMapping("/offer")
    public List<Map<Descriptors.FieldDescriptor, Object>> getOffers() throws InterruptedException{

        return offerClientService.getAllOffers();
    }

    @GetMapping("/{id}")
    public Map<String, Object> getOfferWithProduct(@PathVariable("id") Integer id) throws IOException {
        return offerClientService.getOfferWithProduct(id);
    }
}