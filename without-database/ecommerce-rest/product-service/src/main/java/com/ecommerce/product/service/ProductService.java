package com.ecommerce.product.service;

import com.ecommerce.product.collection.Product;
import com.ecommerce.product.collection.request.ProductRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductService {
    private final List<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        products.add(product);
    }

    public Product saveProduct(ProductRequest productRequest) {
        Double currentPrice = null;
        if (productRequest.getDiscountOffer() != null || productRequest.getDiscountOffer() > 0){
            Double discountPrice = (productRequest.getDiscountOffer()*productRequest.getPrice())/100;
            currentPrice = productRequest.getPrice()-discountPrice;
        }
        Product product = new Product().builder()
                .id(products.size()+1)
                .productCode(productRequest.getProductCode())
                .productTitle(productRequest.getProductTitle())
                .imageUrl(productRequest.getImageUrl())
                .discountOffer(productRequest.getDiscountOffer())
                .price(productRequest.getPrice())
                .currentPrice(currentPrice)
                .build();

        addProduct(product);
        return product;
    }

    public List<Product> getAllProducts() { return products; }

    public Optional<Product> getProductById(Integer productId) {
        return products.stream()
                .filter(product -> product.getId() == productId)
                .findFirst();
    }

    public Product updatePrice(Integer id, Double price) {
        if (price <= 0)
            return null;
        Optional<Product> productOpt = getProductById(id);
        if(productOpt.isPresent()){
            Product product = productOpt.get();
            if (product.getDiscountOffer() != null && product.getDiscountOffer() > 0){
                Double discountPrice = (product.getDiscountOffer()*price)/100;
                product.setPrice(price);
                product.setCurrentPrice(price-discountPrice);
            } else {
                product.setPrice(price);
                product.setCurrentPrice(price);
            }
            return product;
        } else {
            return null;
        }
    }

    public Product saveOffer(Product productReq) {
        Optional<Product> productOpt = getProductById(productReq.getId());
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setProductCode(productReq.getProductCode());
            product.setProductTitle(productReq.getProductTitle());
            product.setImageUrl(productReq.getImageUrl());
            product.setDiscountOffer(productReq.getDiscountOffer());
            product.setPrice(productReq.getPrice());
            product.setCurrentPrice(productReq.getCurrentPrice());
            return product;
        }
        return productReq;
    }
}
