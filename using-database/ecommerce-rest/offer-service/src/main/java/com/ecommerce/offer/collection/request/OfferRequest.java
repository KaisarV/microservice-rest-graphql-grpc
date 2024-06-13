package com.ecommerce.offer.collection.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferRequest {
    @Id
    private Integer id;
    private Integer productId;
    private Double discountOffer;
}
