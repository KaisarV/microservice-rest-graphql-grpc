package com.ecommerce.product.collection;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private int id;
    private String productCode;
    private String productTitle;
    private String imageUrl;
    private Double discountOffer;
    private Double price;
    private Double currentPrice;
}
