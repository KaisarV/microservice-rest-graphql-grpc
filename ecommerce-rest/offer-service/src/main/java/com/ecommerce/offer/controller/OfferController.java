package com.ecommerce.offer.controller;

import com.ecommerce.offer.VO.ResponseTemplateVO;
import com.ecommerce.offer.collection.Offer;
import com.ecommerce.offer.collection.request.OfferRequest;
import com.ecommerce.offer.service.OfferService;
import com.ecommerce.offer.service.SequenceGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/offer-rest")
@Slf4j
@CrossOrigin(origins="http://localhost:3000", allowedHeaders="*")
public class OfferController {

    @Autowired
    private OfferService offerService;

    @Autowired
    private SequenceGeneratorService service;

    @PostMapping("/offer")
    public void addProductOffer(@RequestBody OfferRequest offerRequest) {
        offerRequest.setId(service.getSequenceNumber(Offer.SEQUENCE_NAME));
        offerService.addProductOffer(offerRequest);
    }

    @GetMapping("/offer")
    public List<Offer> getOffers(){
        return offerService.getOffers();
    }

    @GetMapping("offer/{id}")
    public ResponseTemplateVO getOfferWithProduct(@PathVariable("id") Integer id) {
        return offerService.getOfferWithProduct(id);
    }
}