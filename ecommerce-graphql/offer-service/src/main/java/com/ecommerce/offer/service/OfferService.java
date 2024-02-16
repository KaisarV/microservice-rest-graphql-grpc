package com.ecommerce.offer.service;

import com.ecommerce.offer.VO.Product;
import com.ecommerce.offer.VO.ResponseTemplateVO;
import com.ecommerce.offer.collection.Offer;
import com.ecommerce.offer.collection.Response;
import com.ecommerce.offer.collection.request.OfferRequest;
import com.ecommerce.offer.repository.OfferRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OfferService {
    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final HttpGraphQlClient httpGraphQlClient;

    public OfferService(HttpGraphQlClient httpGraphQlClient) {
        this.httpGraphQlClient = httpGraphQlClient;
    }

    public Response addProductOffer(OfferRequest offerRequest) {
        Optional<Offer> offer = offerRepository.findByProductId(offerRequest.getProductId());
        ResponseTemplateVO vo = new ResponseTemplateVO();

        if(offer.isPresent()){
            offer.get().setDiscountOffer(offerRequest.getDiscountOffer());
        } else {
            offer = Optional.ofNullable(new Offer().builder()
                    .id(offerRequest.getId())
                    .productId(offerRequest.getProductId())
                    .discountOffer(offerRequest.getDiscountOffer())
                    .build());
        }

        offerRepository.save(offer.get());

        // Buat body request GraphQL
        String graphqlRequestBody = "{ \"query\": \"mutation { addOffer(addOfferRequest: {id: " + offerRequest.getId() + ", productId: "  + offerRequest.getProductId() +
                ", discountOffer: " + offerRequest.getDiscountOffer() + "}) { productTitle, imageUrl, discountOffer, price, currentPrice }}\" }";
        // Buat header untuk request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Buat entity untuk request
        HttpEntity<String> requestEntity = new HttpEntity<>(graphqlRequestBody, headers);

        System.out.println("AAAAAAAAAAAAAAA");
        // Panggil API GraphQL
        String graphqlUrl = "http://localhost:8081/graphql"; // Sesuaikan dengan URL GraphQL Anda
        String response = restTemplate.exchange(graphqlUrl, HttpMethod.POST, requestEntity, String.class).getBody();

        System.out.println(response);

        ResponseEntity<String> responseEntity = restTemplate.exchange(graphqlUrl, HttpMethod.POST, requestEntity, String.class);

        // Dapatkan status respons dari responsEntity
        HttpStatus status = responseEntity.getStatusCode();

        Response response2 = new Response();
        response2.setStatus(status.value());
        if (status.is2xxSuccessful()) {
            response2.setMessage("Success");
        } else {
            response2.setMessage("Error");
        }

        return response2;
    }

    public List<Offer> getOffers() {
        return offerRepository.findAll();
    }

    public ResponseTemplateVO getOfferWithProduct(Integer id) {
        ResponseTemplateVO vo = new ResponseTemplateVO();
        Offer offer = offerRepository.findByid(id);

        Product product = restTemplate.getForObject("http://localhost:8081/product-rest/product/" + offer.getProductId(),Product.class);

        vo.setOffer(offer);
        vo.setProduct(product);

        return vo;
    }
}
