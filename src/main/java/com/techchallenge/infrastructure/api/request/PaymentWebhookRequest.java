package com.techchallenge.infrastructure.api.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record PaymentWebhookRequest(String resource, @JsonIgnore String topic, @JsonIgnore String action) {

}
