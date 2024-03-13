package com.devproblems.grpc.client.service;

import com.devProblems.*;

import com.devproblems.grpc.client.repository.OfferRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Optional;


@GrpcService
public class OfferServerService extends OfferServiceGrpc.OfferServiceImplBase{
    @Autowired
    private OfferRepository offerRepository;


    @GrpcClient("grpc-product-service")
    ProductServiceGrpc.ProductServiceBlockingStub synchronousClient;



    @Override
    public void addProductOffer(OfferRequest request,
                                StreamObserver<Product> responseObserver) {

        Optional<com.devproblems.grpc.client.collection.Offer> offer = offerRepository.findByProductId(request.getProductId());

        if(offer.isPresent()){
            offer.get().setDiscountOffer(request.getDiscountOffer());
        }else {
            offer = Optional.ofNullable(new com.devproblems.grpc.client.collection.Offer().builder()
                    .id(request.getId())
                    .productId(request.getProductId())
                    .discountOffer(request.getDiscountOffer())
                    .build());
        }

        offerRepository.save(offer.get());

        Product productRequest = Product.newBuilder().setId(request.getProductId()).build();
        Product productResponse = synchronousClient.getProductById(productRequest);

        Double discountPrice = (request.getDiscountOffer()*productResponse.getPrice())/100;

        Product product2 = Product.newBuilder().
                setId(productResponse.getId()).
                setProductCode(productResponse.getProductCode()).
                setProductTitle(productResponse.getProductTitle()).
                setImageUrl(productResponse.getImageUrl()).
                setDiscountOffer(request.getDiscountOffer()).
                setPrice(productResponse.getPrice()).
                setCurrentPrice(productResponse.getPrice()-discountPrice)
                .build();


        Product product = synchronousClient.saveOffer(product2);

        responseObserver.onNext(product);
        responseObserver.onCompleted();
    }

    @Override
    public void getOffers(Empty request,
                          StreamObserver<Offer> responseObserver) {
        List<com.devproblems.grpc.client.collection.Offer> offer1 =  offerRepository.findAll();

        for (com.devproblems.grpc.client.collection.Offer o : offer1) {
            Offer offer = Offer.newBuilder().
                    setId(o.getId()).
                    setProductId(o.getProductId()).
                    setDiscountOffer(o.getDiscountOffer())
                    .build();

            responseObserver.onNext(offer);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getOfferWithProduct(Offer request,
                                    StreamObserver<ResponseTemplateVO> responseObserver) {
        int offerId = request.getId();
        System.out.println("Ini dipanggil2");
        com.devproblems.grpc.client.collection.Offer offer = offerRepository.findByid(offerId);
        Product productRequest = Product.newBuilder().setId(offer.getProductId()).build();
        Product productResponse = synchronousClient.getProductById(productRequest);

        Offer offer1 = Offer.newBuilder().setId(offer.getId()).setDiscountOffer(offer.getDiscountOffer()).setProductId(offer.getProductId()).build();

        ResponseTemplateVO responseTemplateVO = ResponseTemplateVO.newBuilder().setOffer(offer1).setProduct(productResponse).build();

        responseObserver.onNext(responseTemplateVO);
        responseObserver.onCompleted();
    }
}
