package com.ecommerce.product.service;

import com.ecommerce.product.collection.Product;
import com.ecommerce.product.collection.request.ProductRequest;
import com.ecommerce.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Product saveProduct(ProductRequest productRequest) {
        Double currentPrice = null;
        if (productRequest.getDiscountOffer() != null || productRequest.getDiscountOffer() > 0){
            Double discountPrice = (productRequest.getDiscountOffer()*productRequest.getPrice())/100;
            currentPrice = productRequest.getPrice()-discountPrice;
        }
        Product product = new Product().builder()
                .id(productRequest.getId())
                .productCode(productRequest.getProductCode())
                .productTitle(productRequest.getProductTitle())
                .imageUrl(productRequest.getImageUrl())
                .discountOffer(productRequest.getDiscountOffer())
                .price(productRequest.getPrice())
                .currentPrice(currentPrice)
                .build();

        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAllByOrderByPriceAsc();
    }

    public Optional<Product> getProductById(Integer productId) {

        return productRepository.findById(productId);
    }

    public void addProductOffer(Integer productId, Double discountOffer) {
        Optional<Product> product = getProductById(productId);
        if(product.isPresent() && product.get().getPrice() != null){
            Double discountPrice = (discountOffer*product.get().getPrice())/100;
            product.get().setCurrentPrice(product.get().getPrice()-discountPrice);
            product.get().setDiscountOffer(discountOffer);
            productRepository.save(product.get());
        }
    }

    public Product updatePrice(Integer id, Double price) {
        if (price <= 0)
            return null;
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent()){
            if (product.get().getDiscountOffer() != null && product.get().getDiscountOffer() > 0){
                Double discountPrice = (product.get().getDiscountOffer()*price)/100;
                product.get().setPrice(price);
                product.get().setCurrentPrice(price-discountPrice);
            } else {
                product.get().setPrice(price);
                product.get().setCurrentPrice(price);
            }
            return productRepository.save(product.get());
        } else {
            return null;
        }
    }

    public Product saveOffer(Product product) {
        return productRepository.save(product);
    }

}
