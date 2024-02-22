package com.devproblems.grpc.client.VO;

import com.devproblems.grpc.client.collection.Offer;
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
