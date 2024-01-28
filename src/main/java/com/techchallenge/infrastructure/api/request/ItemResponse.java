package com.techchallenge.infrastructure.api.request;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ItemResponse(
        String skuNumber,
        String category,
        String title,
        String description,
        BigDecimal unitPrice,
        Integer quantity,
        String unitMeasure,
        BigDecimal totalAmount) {


}
