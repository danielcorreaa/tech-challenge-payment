package com.techchallenge.domain.entity;

import com.techchallenge.domain.valueobject.Validation;

public class MessagePayment {

    private String externalReference;
    private String orderStatus;

    public MessagePayment(String externalReference, String orderStatus) {
        this.externalReference = Validation.validateExternalReference(externalReference);
        this.orderStatus = Validation.validateOrderStatus(orderStatus);
    }

    public String getExternalReference() {
        return externalReference;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
