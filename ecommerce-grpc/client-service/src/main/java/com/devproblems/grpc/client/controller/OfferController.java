package com.devproblems.grpc.client.controller;

import com.devProblems.ResponseTemplateVO;
import com.devproblems.grpc.client.collection.Offer;
import com.devproblems.grpc.client.collection.request.OfferRequest;
import com.devproblems.grpc.client.service.OfferClientService;
import com.devproblems.grpc.client.service.SequenceGeneratorService;
import com.google.protobuf.Descriptors;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@AllArgsConstructor
public class OfferController {

    final OfferClientService offerClientService;

    @Autowired
    private SequenceGeneratorService service;

    @PostMapping("/offer")
    public void addProductOffer(@RequestBody OfferRequest offerRequest) {
        offerRequest.setId(service.getSequenceNumber(Offer.SEQUENCE_NAME));

        com.devProblems.OfferRequest offerRequest1 = com.devProblems.OfferRequest.
                newBuilder().
                setId(offerRequest.getId()).
                setProductId(offerRequest.getProductId()).
                setDiscountOffer(offerRequest.getDiscountOffer()).build();

        offerClientService.addProductOffer(offerRequest1);
    }

    @GetMapping("/offer")
    public List<Map<Descriptors.FieldDescriptor, Object>> getOffers() throws InterruptedException{

        return offerClientService.getAllOffers();
    }

    @GetMapping("/{id}")
    public Map<Descriptors.FieldDescriptor, Object> getOfferWithProduct(@PathVariable("id") Integer id) {
        return offerClientService.getOfferWithProduct(id);
    }


}
