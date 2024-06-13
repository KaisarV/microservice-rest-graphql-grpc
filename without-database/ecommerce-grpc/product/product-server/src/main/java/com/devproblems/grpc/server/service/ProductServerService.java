package com.devproblems.grpc.server.service;

import com.devProblems.*;
import com.devproblems.grpc.server.repository.ProductRepository;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;



@GrpcService
public class ProductServerService extends ProductServiceGrpc.ProductServiceImplBase {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SequenceGeneratorService service;

    @Override
    public void getProductById(Product request, StreamObserver<Product> responseObserver) {
        Optional<com.devproblems.grpc.server.collection.Product> product1 = productRepository.findById(request.getId());

        if (product1.isPresent()) {
            Product product2 = Product.newBuilder()
                    .setId(product1.get().getId())
                    .setProductCode(product1.get().getProductCode())
                    .setProductTitle(product1.get().getProductTitle())
                    .setImageUrl(product1.get().getImageUrl())
                    .setDiscountOffer(product1.get().getDiscountOffer())
                    .setPrice(product1.get().getPrice())
                    .setCurrentPrice(product1.get().getCurrentPrice())
                    .build();

            responseObserver.onNext(product2);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getAllProducts(Empty request,
                               StreamObserver<ProductsResponse> responseObserver) {
        List<com.devproblems.grpc.server.collection.Product> productList = productRepository.findAllByOrderByPriceAsc();
        ProductsResponse.Builder productsResponseBuilder = ProductsResponse.newBuilder();

        for (com.devproblems.grpc.server.collection.Product p : productList) {
            Product grpcProduct = Product.newBuilder().
                    setId(p.getId()).
                    setProductCode(p.getProductCode()).
                    setProductTitle(p.getProductTitle()).
                    setImageUrl(p.getImageUrl()).
                    setDiscountOffer(p.getDiscountOffer()).
                    setPrice(p.getPrice()).
                    setCurrentPrice(p.getCurrentPrice())
                    .build();
            productsResponseBuilder.addProducts(grpcProduct);
        }

        ProductsResponse productsResponse = productsResponseBuilder.build();
        responseObserver.onNext(productsResponse);
        responseObserver.onCompleted();
    }


    @Override
    public void saveProduct(ProductRequest productRequest,
                            StreamObserver<com.devProblems.Product> responseObserver) {
        Double currentPrice = null;

        if (productRequest.getDiscountOffer() != 0 && productRequest.getDiscountOffer() > 0){
            Double discountPrice = (productRequest.getDiscountOffer()*productRequest.getPrice())/100;
            currentPrice = productRequest.getPrice()-discountPrice;
        }else {
            currentPrice =productRequest.getPrice();
        }

        com.devproblems.grpc.server.collection.Product product = new com.devproblems.grpc.server.collection.Product().builder()
                .id(service.getSequenceNumber(com.devproblems.grpc.server.collection.Product.SEQUENCE_NAME))
                .productCode(productRequest.getProductCode())
                .productTitle(productRequest.getProductTitle())
                .imageUrl(productRequest.getImageUrl())
                .discountOffer(productRequest.getDiscountOffer())
                .price(productRequest.getPrice())
                .currentPrice(currentPrice)
                .build();

        com.devproblems.grpc.server.collection.Product product2 = productRepository.save(product);


        Product product3 = Product.newBuilder().
                setId(product2.getId()).
                setProductCode(product2.getProductCode()).
                setProductTitle(product2.getProductTitle()).
                setImageUrl(product2.getImageUrl()).
                setDiscountOffer(product2.getDiscountOffer()).
                setPrice(product2.getPrice()).
                setCurrentPrice(currentPrice)
                .build();

        responseObserver.onNext(product3);
        responseObserver.onCompleted();
    }

    @Override
    public void saveOffer(Product request,
                          StreamObserver<Product> responseObserver) {

        com.devproblems.grpc.server.collection.Product product=
                new com.devproblems.grpc.server.collection.Product(request.getId(),
                        request.getProductCode(),
                        request.getProductTitle(),
                        request.getImageUrl(),
                        request.getDiscountOffer(),
                        request.getPrice(),
                        request.getCurrentPrice());


        com.devproblems.grpc.server.collection.Product product1 = productRepository.save(product);

        Product product2 = Product.newBuilder().
                setId(product1.getId()).
                setProductCode(product1.getProductCode()).
                setProductTitle(product1.getProductTitle()).
                setImageUrl(product1.getImageUrl()).
                setDiscountOffer(product1.getDiscountOffer()).
                setPrice(product1.getPrice()).
                setCurrentPrice(product1.getCurrentPrice())
                .build();

        responseObserver.onNext(product2);
        responseObserver.onCompleted();
    }
    @Override
    public void addPrice(AddPriceRequest request,
                         StreamObserver<Product> responseObserver) {
        if (request.getPrice() <= 0)
            return;

        Optional<com.devproblems.grpc.server.collection.Product> product = productRepository.findById(request.getId());

        if(product.isPresent()){
            if (product.get().getDiscountOffer() != null && product.get().getDiscountOffer() > 0){
                Double discountPrice = (product.get().getDiscountOffer()*request.getPrice())/100;
                System.out.println(discountPrice);
                product.get().setPrice(request.getPrice());
                product.get().setCurrentPrice(request.getPrice()-discountPrice);
            } else {
                product.get().setPrice(request.getPrice());
            }
            com.devproblems.grpc.server.collection.Product product1 = productRepository.save(product.get());

            Product product2 = Product.newBuilder().
                    setId(product1.getId()).
                    setProductCode(product1.getProductCode()).
                    setProductTitle(product1.getProductTitle()).
                    setImageUrl(product1.getImageUrl()).
                    setDiscountOffer(product1.getDiscountOffer()).
                    setPrice(product1.getPrice()).
                    setCurrentPrice(product1.getCurrentPrice())
                    .build();

            responseObserver.onNext(product2);
            responseObserver.onCompleted();
        }
    }
}
