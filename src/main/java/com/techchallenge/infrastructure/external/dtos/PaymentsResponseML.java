package com.techchallenge.infrastructure.external.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentsResponseML(
		Long id, 
		@JsonProperty("transaction_amount")
		String transactionAmount,
		String status) {

}
