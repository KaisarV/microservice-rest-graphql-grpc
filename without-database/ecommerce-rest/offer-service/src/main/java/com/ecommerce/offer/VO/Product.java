package com.ecommerce.offer.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Integer id;
    private String productCode;
    private String productTitle;
    private String imageUrl;
    private Double discountOffer;
    private Double price;
    private Double currentPrice;
}
