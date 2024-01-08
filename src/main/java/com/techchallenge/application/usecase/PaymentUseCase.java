package com.techchallenge.application.usecase;

import com.techchallenge.domain.entity.Payment;
import com.techchallenge.domain.entity.PaymentQRCode;

public interface PaymentUseCase {

	void save(Payment payment);

	Payment findByExternalReference(String externalReference);

	PaymentQRCode generatePayment(String order, String uriString);

	void webhook(String resource);

	
}
