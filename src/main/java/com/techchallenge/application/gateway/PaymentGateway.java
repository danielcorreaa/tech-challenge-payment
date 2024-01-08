package com.techchallenge.application.gateway;

import com.techchallenge.domain.entity.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentGateway {
	
	Payment insert(Payment payment);
	Optional<Payment> findById(String order);
	public List<Payment> findNotSendAndIsPaid();
}
