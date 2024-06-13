package com.ecommerce.apigateway.collection.request;

import com.ecommerce.apigateway.collection.Offer;
import com.ecommerce.apigateway.collection.Product;
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
