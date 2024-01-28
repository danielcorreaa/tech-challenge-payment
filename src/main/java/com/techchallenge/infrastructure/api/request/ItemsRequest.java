package com.techchallenge.infrastructure.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record ItemsRequest(
		@JsonProperty("sku_number")
		String skuNumber, 
		String category, 
		String title, 
		String description,
		@JsonProperty("unit_price")
		BigDecimal unitPrice,
		Integer quantity,
		@JsonProperty("unit_measure")
		String unitMeasure,
		@JsonProperty("total_amount")
		BigDecimal totalAmount) {

}
