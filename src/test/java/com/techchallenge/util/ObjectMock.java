package com.techchallenge.util;

import com.techchallenge.domain.entity.Payment;
import com.techchallenge.domain.valueobject.Item;

import java.math.BigDecimal;
import java.util.List;

public class ObjectMock {

    public Payment getPaymentMock(String id){
        return new Payment(id, "test", "test", null,
                List.of(getitemMock("2"), getitemMock("3")));
    }

    public Item getitemMock(String id){
        return new Item(id, "test", "test", "test",
                new BigDecimal("20"), 2);
    }

    public List<Payment> getPayments(){
        return  List.of(
                getPaymentMock("523001"),
                getPaymentMock("523002"),
                getPaymentMock("523003"),
                getPaymentMock("523004"),
                getPaymentMock("523005")
        );
    }

    public List<Payment> getPaymentsToPaid(){
        return getPayments().stream().map(
                pay -> pay.changeStatus("paid")
        ).toList();
    }
}
