package com.techchallenge.domain.entity;

import com.techchallenge.domain.valueobject.Item;
import com.techchallenge.domain.valueobject.Validation;

import java.util.List;

public class MessagePayment {

    private String externalReference;
    private String orderStatus;
    private String cpfCustomer;
    private List<Item> itens;


    public MessagePayment(String externalReference, String orderStatus, List<Item> itens) {
        this.externalReference = Validation.validateExternalReference(externalReference);
        this.orderStatus = Validation.validateOrderStatus(orderStatus);
        this.itens = itens;
    }

    public MessagePayment(String externalReference, String orderStatus, String cpfCustomer) {
        this.externalReference = Validation.validateExternalReference(externalReference);
        this.orderStatus = orderStatus;
        this.cpfCustomer = cpfCustomer;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getCpfCustomer() {
        return cpfCustomer;
    }

    public List<Item> getItens() {
        return itens;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
