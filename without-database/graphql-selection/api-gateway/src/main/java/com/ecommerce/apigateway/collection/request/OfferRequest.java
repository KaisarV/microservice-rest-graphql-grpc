package com.ecommerce.apigateway.collection.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferRequest {
    private Integer id;
    private Integer productId;
    private Double discountOffer;
}
