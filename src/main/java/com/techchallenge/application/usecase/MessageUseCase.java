package com.techchallenge.application.usecase;

import com.techchallenge.domain.entity.MessagePayment;
import org.springframework.kafka.support.SendResult;

public interface MessageUseCase {
    SendResult<String, MessagePayment> send(MessagePayment messagePayment);
}
