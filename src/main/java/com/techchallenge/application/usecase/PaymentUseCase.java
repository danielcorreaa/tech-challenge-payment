package com.techchallenge.application.usecase;

import com.techchallenge.domain.entity.Payment;
import com.techchallenge.domain.entity.PaymentQRCode;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentUseCase {

	void save(Payment payment);

	Payment findByExternalReference(String externalReference);

	PaymentQRCode generatePayment(String order, String uriString, Long minuteToExpirations);

	void webhook(String resource);

	List<Payment> findNotSendAndIsPaid();

	List<Payment> findPaymentExpired(LocalDateTime now);

    void create(Payment payment);
}
