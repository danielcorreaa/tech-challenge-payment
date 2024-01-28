package com.techchallenge.infrastructure.external.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OrderResponseML(
		@JsonProperty("in_store_order_id") 
		String inStoreOrderId,
		@JsonProperty("qr_data") 
		String QrData) {

	

}
