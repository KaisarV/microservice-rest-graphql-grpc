package com.ecommerce.offer.service;

import com.ecommerce.offer.VO.Product;
import com.ecommerce.offer.VO.ResponseTemplateVO;
import com.ecommerce.offer.collection.Offer;
import com.ecommerce.offer.collection.request.OfferRequest;
import com.ecommerce.offer.repository.OfferRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.springframework.util.ClassUtils.isPresent;

@Service
@Slf4j
public class OfferService {
    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SequenceGeneratorService service;

    public Product addProductOffer(OfferRequest offerRequest) {
        Optional<Offer> offer = offerRepository.findByProductId(offerRequest.getProductId());

        if(offer.isPresent()){
            offer.get().setDiscountOffer(offerRequest.getDiscountOffer());
        }else {
            offer = Optional.ofNullable(new Offer().builder()
                    .id(service.getSequenceNumber(Offer.SEQUENCE_NAME))
                    .productId(offerRequest.getProductId())
                    .discountOffer(offerRequest.getDiscountOffer())
                    .build());
        }

        offerRepository.save(offer.get());

        Product product = restTemplate.getForObject("http://product-rest:8081/product-rest/product/"
                + offerRequest.getProductId(),Product.class);

        Double discountPrice = (offerRequest.getDiscountOffer()*product.getPrice())/100;
        product.setCurrentPrice(product.getPrice()-discountPrice);
        product.setDiscountOffer(offerRequest.getDiscountOffer());

        Product product2 = restTemplate.postForObject("http://product-rest:8081/product-rest/addOffer", product, Product.class);

        return product2;
    }

    public List<Offer> getOffers() {
        return offerRepository.findAll();
    }

    public ResponseTemplateVO getOfferWithProduct(Integer id) {
        ResponseTemplateVO vo = new ResponseTemplateVO();
        Offer offer = offerRepository.findByid(id);

        Product product = restTemplate.getForObject("http://product-rest:8081/product-rest/product/" + offer.getProductId(),Product.class);

        vo.setOffer(offer);
        vo.setProduct(product);

        return vo;
    }
}
