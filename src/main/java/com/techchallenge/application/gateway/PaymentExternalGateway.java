package com.techchallenge.application.gateway;

import com.techchallenge.domain.entity.Payment;
import com.techchallenge.domain.entity.PaymentQRCode;
import com.techchallenge.infrastructure.external.dtos.PaymentResponseML;

import java.util.Optional;

public interface PaymentExternalGateway {

    Optional<PaymentQRCode> sendPayment(Payment payment);

	PaymentResponseML checkPayment(String resource);
}
