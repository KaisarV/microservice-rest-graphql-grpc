package com.ecommerce.offer.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "offer-rest")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Offer {
    @Transient
    public static final String SEQUENCE_NAME = "user_sequence";

    @Id
    private Integer id;

    private Integer productId;

    private Double discountOffer;
}
