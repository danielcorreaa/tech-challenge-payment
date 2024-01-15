package com.techchallenge.util;

import com.techchallenge.domain.entity.Payment;
import com.techchallenge.domain.valueobject.Item;
import com.techchallenge.infrastructure.external.dtos.OrderResponseML;
import com.techchallenge.infrastructure.external.dtos.PaymentResponseML;
import com.techchallenge.infrastructure.message.consumer.dto.OrderDto;

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

    public OrderResponseML getOrderResponseML() {
        return new OrderResponseML("26f312a8-a8f1-4378-9161-83efb734a0c2",
                "00020101021243650016COM.MERCADOLIBRE02013063626f312a8-a8f1-4378-9161-83efb734a0c25204000053039865802BR5909Test Test6009SAO PAULO62070503***630409D4");
    }

    public OrderResponseML getOrderResponseMLInvalid() {
        return new OrderResponseML("26f312a8",
                "");
    }

    public PaymentResponseML getPaymentResponseML(String externalReference){
        return  PaymentResponseML.builder().status("paid").externalReference(externalReference).build();
    }


}
