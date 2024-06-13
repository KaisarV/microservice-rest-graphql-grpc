package com.ecommerce.offer.service;

import com.ecommerce.offer.collection.Offer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final OfferService offerService;

    public DataLoader(OfferService offerService) {
        this.offerService = offerService;
    }

    @Override
    public void run(String... args) throws Exception {
        for (int i = 1; i <= 500; i++) {
            Offer offer = new Offer(
                    i,
                    i,
                    0.0
            );
            offerService.addProductOffer(offer);
        }
    }
}
