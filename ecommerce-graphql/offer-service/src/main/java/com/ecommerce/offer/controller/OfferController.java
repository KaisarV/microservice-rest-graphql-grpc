package com.ecommerce.offer.controller;

import com.ecommerce.offer.VO.ResponseTemplateVO;
import com.ecommerce.offer.collection.Offer;
import com.ecommerce.offer.collection.Response;
import com.ecommerce.offer.collection.request.OfferRequest;
import com.ecommerce.offer.service.OfferService;
import com.ecommerce.offer.service.SequenceGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class OfferController {

    @Autowired
    private OfferService offerService;

    @Autowired
    private SequenceGeneratorService service;

    private final HttpGraphQlClient httpGraphQlClient;

    public OfferController(HttpGraphQlClient httpGraphQlClient) {
        this.httpGraphQlClient = httpGraphQlClient;
    }

    @MutationMapping
    public Response addOffer(@Argument OfferRequest offerRequest) {
        offerRequest.setId(service.getSequenceNumber(Offer.SEQUENCE_NAME));
        return offerService.addProductOffer(offerRequest);
    }

    @QueryMapping
    public List<Offer> allOffers(){
        return offerService.getOffers();
    }

    @QueryMapping
    public ResponseTemplateVO offerWithProduct(@Argument Integer id) {
        return offerService.getOfferWithProduct(id);
    }
}
