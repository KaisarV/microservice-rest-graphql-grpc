package com.ecommerce.apigateway.controller;

import com.ecommerce.apigateway.collection.Offer;
import com.ecommerce.apigateway.collection.Product;
import com.ecommerce.apigateway.collection.request.OfferRequest;
import com.ecommerce.apigateway.collection.request.ResponseTemplateVO;
import com.ecommerce.apigateway.service.OfferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/offer-graphql")
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
