package com.ecommerce.offer.VO;

import com.ecommerce.offer.collection.Offer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTemplateVO {
    private Offer offer;
    private Product product;
}
