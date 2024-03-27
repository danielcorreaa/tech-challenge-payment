package com.techchallenge.infrastructure.api.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record PaymentWebhookRequest(String resource,  String topic,  String action) {


    @Override
    public String toString() {
        return "PaymentWebhookRequest{" +
                "resource='" + resource + '\'' +
                ", topic='" + topic + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}
