package com.techchallenge.application.usecase.interactor;

import com.techchallenge.application.gateway.MessageGateway;
import com.techchallenge.application.gateway.PaymentGateway;
import com.techchallenge.application.usecase.MessageUseCase;
import com.techchallenge.domain.entity.MessagePayment;
import com.techchallenge.domain.entity.Payment;

import java.util.List;

public class MessageUseCaseInteractor implements MessageUseCase {

    private PaymentGateway paymentGateway;
    private MessageGateway messageGateway;

    public MessageUseCaseInteractor(PaymentGateway paymentGateway, MessageGateway messageGateway) {
        this.paymentGateway = paymentGateway;
        this.messageGateway = messageGateway;
    }

    @Override
    public void send(MessagePayment messagePayment) {
        messageGateway.send(messagePayment);
    }

    @Override
    public List<Payment> findNotSendAndIsPaid() {
        return paymentGateway.findNotSendAndIsPaid();
    }


}
