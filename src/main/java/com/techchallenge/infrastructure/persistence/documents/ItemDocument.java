package com.techchallenge.infrastructure.persistence.documents;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ItemDocument {

    private String skuNumber;
    private String category;
    private String title;
    private String description;
    private BigDecimal unitPrice;
    private Integer quantity;
    private String unitMeasure;
    private BigDecimal totalAmount;

}
