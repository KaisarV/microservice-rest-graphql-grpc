package com.ecommerce.product.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "product-rest")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product {
    @Transient
    public static final String SEQUENCE_NAME = "user_sequence";

    @Id
    private Integer id;
    private String productCode;
    private String productTitle;
    private String imageUrl;
    private Double discountOffer;
    private Double price;
    private Double currentPrice;
}