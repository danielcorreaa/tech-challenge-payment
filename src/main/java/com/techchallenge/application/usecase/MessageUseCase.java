package com.techchallenge.application.usecase;

import com.techchallenge.domain.entity.MessagePayment;
import com.techchallenge.domain.entity.Payment;
import org.springframework.kafka.support.SendResult;

import java.util.List;

public interface MessageUseCase {
    SendResult<String, MessagePayment> send(MessagePayment messagePayment);
}
