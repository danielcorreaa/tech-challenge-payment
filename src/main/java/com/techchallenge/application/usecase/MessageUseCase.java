package com.techchallenge.application.usecase;

import com.techchallenge.domain.entity.MessagePayment;
import com.techchallenge.domain.entity.Payment;

import java.util.List;

public interface MessageUseCase {
    void send(MessagePayment messagePayment);
    List<Payment> findNotSendAndIsPaid();
}
