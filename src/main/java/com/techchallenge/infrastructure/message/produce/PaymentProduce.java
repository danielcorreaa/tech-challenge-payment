package com.techchallenge.infrastructure.message.produce;

import com.techchallenge.application.usecase.MessageUseCase;
import com.techchallenge.application.usecase.PaymentUseCase;
import com.techchallenge.domain.entity.MessagePayment;
import com.techchallenge.domain.entity.Payment;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class PaymentProduce {

    private MessageUseCase messageUseCase;
    private PaymentUseCase paymentUseCase;

    public PaymentProduce(MessageUseCase messageUseCase, PaymentUseCase paymentUseCase) {
        this.messageUseCase = messageUseCase;
        this.paymentUseCase = paymentUseCase;
    }

    public void process(){
        List<Payment> notSendAndIsPaid = messageUseCase.findNotSendAndIsPaid();
        if(notSendAndIsPaid.isEmpty()) {
            log.info("No message to sendd");
        }
        notSendAndIsPaid.forEach( send -> {
            log.info("Message to send: " + send);
            messageUseCase.send(new MessagePayment(send.getExternalReference(), send.getOrderStatus()));
            send.toSend();
            paymentUseCase.save(send);
        });
    }
}
