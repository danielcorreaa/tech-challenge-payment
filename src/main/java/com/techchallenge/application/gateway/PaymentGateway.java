package com.techchallenge.application.gateway;

import com.techchallenge.domain.entity.Payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentGateway {
	
	Payment insert(Payment payment);
	Optional<Payment> findById(String order);
	List<Payment> findNotSendAndIsPaid();
    List<Payment> findPaymentExpired(LocalDateTime now);
}
