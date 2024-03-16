package com.techchallenge.infrastructure.api.request;

import jakarta.validation.constraints.NotBlank;

public record PayRequest(
        @NotBlank(message = "Order identifier is required")
        String externalReference) {
}
