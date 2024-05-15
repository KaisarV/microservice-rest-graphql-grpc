package com.ecommerce.offer.controller;

import com.ecommerce.offer.VO.Product;
import com.ecommerce.offer.VO.ResponseTemplateVO;
import com.ecommerce.offer.collection.Offer;
import com.ecommerce.offer.collection.request.OfferRequest;
import com.ecommerce.offer.service.OfferService;
import com.ecommerce.offer.service.SequenceGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/offer-rest")
@Slf4j
@CrossOrigin(origins="http://localhost:3000", allowedHeaders="*")
public class OfferController {

    @Autowired
    private OfferService offerService;



    @PostMapping("/offer")
    public Product addProductOffer(@RequestBody OfferRequest offerRequest) {
        return offerService.addProductOffer(offerRequest);
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