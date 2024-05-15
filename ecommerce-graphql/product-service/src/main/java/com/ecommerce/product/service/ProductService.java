package com.ecommerce.product.service;

import com.ecommerce.product.collection.Product;
import com.ecommerce.product.collection.request.AddPriceRequest;
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
        Double currentPrice = calculateCurrentPrice(productRequest.getPrice(), productRequest.getDiscountOffer());

        Product product = Product.builder()
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

    public Product addProductOffer(Integer productId, Double discountOffer) {
        return getProductById(productId).map(product -> {
            product.setDiscountOffer(discountOffer);
            product.setCurrentPrice(calculateCurrentPrice(product.getPrice(), discountOffer));
            return productRepository.save(product);
        }).orElse(null);
    }

    public Product addPrice(AddPriceRequest addPriceRequest) {
        if (addPriceRequest.getPrice() <= 0) {
            return null;
        }

        return productRepository.findById(addPriceRequest.getId()).map(product -> {
            product.setPrice(addPriceRequest.getPrice());
            product.setCurrentPrice(calculateCurrentPrice(addPriceRequest.getPrice(), product.getDiscountOffer()));
            return productRepository.save(product);
        }).orElse(null);
    }

    private Double calculateCurrentPrice(Double price, Double discountOffer) {
        if (price == null || discountOffer == null || discountOffer <= 0) {
            return price;
        }
        Double discountPrice = (discountOffer * price) / 100;
        return price - discountPrice;
    }

    public Product saveOffer(Product product) {
        return productRepository.save(product);
    }
}
