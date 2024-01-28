package com.techchallenge.infrastructure.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public record PaymentRequest(
        @JsonProperty("external_reference")
        String externalReference,
        String title,
        String description,
        @JsonProperty("notification_url")
        String notificationUrl,
        @JsonProperty("total_amount")
        BigDecimal totalAmount,
        List<ItemsRequest> items
) {

}
