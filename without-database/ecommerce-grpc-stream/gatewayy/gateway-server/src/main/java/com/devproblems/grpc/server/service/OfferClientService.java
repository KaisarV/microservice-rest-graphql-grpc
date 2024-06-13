package com.devproblems.grpc.server.service;


import com.devProblems.*;
import com.google.protobuf.Descriptors;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class OfferClientService {

    @GrpcClient("grpc-offer-service")
    OfferServiceGrpc.OfferServiceBlockingStub synchronousClient;

    @GrpcClient("grpc-offer-service")
    OfferServiceGrpc.OfferServiceStub asynchronousClient;


    public Map<Descriptors.FieldDescriptor, Object> addProductOffer(OfferRequest offerRequest) {
        Product productResponse = synchronousClient.addProductOffer(offerRequest);
        return productResponse.getAllFields();
    }

    public List<Map<Descriptors.FieldDescriptor, Object>> getAllOffers() throws InterruptedException {

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final List<Map<Descriptors.FieldDescriptor, Object>> response = new ArrayList<>();
        asynchronousClient.getOffers(Empty.newBuilder().build(), new StreamObserver<Offer>() {
            @Override
            public void onNext(Offer offer) {
                response.add(offer.getAllFields());
            }

            @Override
            public void onError(Throwable throwable) {
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }
        });

        boolean await = countDownLatch.await(1, TimeUnit.MINUTES);
        return await ? response : Collections.emptyList();
    }

    public Map<String, Object> getOfferWithProduct(int offerId) throws  IOException {
        Offer offerRequest = Offer.newBuilder().setId(offerId).build();
        ResponseTemplateVO offerProductResponse = synchronousClient.getOfferWithProduct(offerRequest);
        return ProtobufUtils.protobufToMap(offerProductResponse);
    }
}
