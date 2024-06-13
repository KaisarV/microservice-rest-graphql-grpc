package com.ecommerce.offer.service;

import com.ecommerce.offer.VO.Product;
import com.ecommerce.offer.VO.ResponseTemplateVO;
import com.ecommerce.offer.collection.Offer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OfferService {

    private final List<Offer> offers = new ArrayList<>();

    @Autowired
    private RestTemplate restTemplate;

    private static final String URL = "http://localhost:8081/";

    public Product addProductOffer(Offer offerRequest) {
        Optional<Offer> offerOpt = getOfferByProductId(offerRequest.getProductId());
        if(offerOpt.isPresent()){
            Offer offer = offerOpt.get();
            offer.setDiscountOffer(offerRequest.getDiscountOffer());
        }else {
            Offer newOffer = new Offer().builder()
                    .id(offers.size()+1)
                    .productId(offerRequest.getProductId())
                    .discountOffer(offerRequest.getDiscountOffer())
                    .build();

            offers.add(newOffer);
        }

        Product product = restTemplate.getForObject(URL + "product-rest/product/"
                + offerRequest.getProductId(),Product.class);

        Double discountPrice = (offerRequest.getDiscountOffer()*product.getPrice())/100;
        product.setCurrentPrice(product.getPrice()-discountPrice);
        product.setDiscountOffer(offerRequest.getDiscountOffer());

        Product product2 = restTemplate.postForObject(URL + "product-rest/addOffer", product, Product.class);

        return product2;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public ResponseTemplateVO getOfferWithProduct(Integer id) {
        ResponseTemplateVO vo = new ResponseTemplateVO();
        Optional<Offer> offerOpt = getOfferById(id);

        Product product = restTemplate.getForObject(URL + "product-rest/product/" + offerOpt.get().getProductId(),Product.class);

        vo.setOffer(offerOpt.get());
        vo.setProduct(product);

        return vo;
    }

    public Optional<Offer> getOfferByProductId(Integer productId) {
        return offers.stream()
                .filter(offer -> offer.getProductId() == productId)
                .findFirst();
    }

    public Optional<Offer> getOfferById(Integer id) {
        return offers.stream()
                .filter(offer -> offer.getId() == id)
                .findFirst();
    }
}
