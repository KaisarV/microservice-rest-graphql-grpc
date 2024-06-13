package com.ecommerce.product.collection.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOfferRequest {
    @Id
    private Integer id;
    private Integer productId;
    private Double discountOffer;
}
