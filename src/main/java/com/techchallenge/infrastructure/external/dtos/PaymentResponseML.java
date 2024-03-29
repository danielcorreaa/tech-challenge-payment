package com.techchallenge.infrastructure.external.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record PaymentResponseML(
		Long id,
		String status,
		@JsonProperty("order_status") 
		String orderStatus,
		@JsonProperty("external_reference") 
		String externalReference,
		@JsonIgnore
		@JsonProperty("preference_id")
		String preferenceId,
		@JsonIgnore
		List<PaymentsResponseML> payments,
		@JsonIgnore
		CollectorResponseML collector,
		@JsonIgnore
		String marketplace,
		@JsonIgnore
		Boolean cancelled,
		@JsonIgnore		
		@JsonProperty("notification_url")		
		String notificationUrl,
		
		@JsonIgnore
		@JsonProperty("date_created")		
		String dateCreated,
		
		@JsonIgnore
		@JsonProperty("last_updated")
		String lastUpdated,
		
		@JsonIgnore
		@JsonProperty("sponsor_id")
		String sponsorId, 
		
		@JsonIgnore
		@JsonProperty("shipping_cost")
		Double shippingCost,
		
		@JsonIgnore
		@JsonProperty("total_amount")
		Double totalAmount,
		
		@JsonIgnore
		@JsonProperty("site_id")
		String siteId,
		
		@JsonIgnore
		@JsonProperty("paid_amount")
		Double paidAmount, 
		
		@JsonIgnore
		@JsonProperty("refunded_amount")
		Double refundedAmount,
		
		@JsonIgnore
		PayerResponseML payer,

		@JsonIgnore
		@JsonProperty("additional_info")
		String additionalInfo,
		
		@JsonIgnore
		@JsonProperty("application_id")
		String applicationId,
		
		@JsonIgnore
		@JsonProperty("is_test")
		Boolean isTest
		) {

}
