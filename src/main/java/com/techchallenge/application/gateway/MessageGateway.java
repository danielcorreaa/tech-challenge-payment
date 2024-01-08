package com.techchallenge.application.gateway;

import com.techchallenge.domain.entity.MessagePayment;
import com.techchallenge.domain.entity.Payment;

public interface MessageGateway {
    void send(MessagePayment message);
}
