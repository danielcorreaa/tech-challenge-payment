package com.techchallenge.application.usecase.interactor;

;
import com.techchallenge.application.gateway.PaymentExternalGateway;
import com.techchallenge.application.gateway.PaymentGateway;
import com.techchallenge.application.usecase.PaymentUseCase;
import com.techchallenge.core.exceptions.BusinessException;
import com.techchallenge.core.exceptions.NotFoundException;
import com.techchallenge.domain.entity.Payment;
import com.techchallenge.domain.entity.PaymentQRCode;
import com.techchallenge.infrastructure.external.dtos.PaymentResponseML;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public class PaymentUseCaseInteractor implements PaymentUseCase {

	private PaymentExternalGateway paymentExternalGateway;
	private PaymentGateway paymentGateway;	;

	public PaymentUseCaseInteractor(PaymentExternalGateway paymentExternalGateway,
                                    PaymentGateway paymentGateway) {
		super();
		this.paymentExternalGateway = paymentExternalGateway;
		this.paymentGateway = paymentGateway;
	}

	@Override
	public void save(Payment payment) {
		paymentGateway.insert(payment);
	}

	@Override
	public Payment findByExternalReference(String externalReference) {
		return paymentGateway.findById(externalReference)
				.orElseThrow(() -> new NotFoundException("Payment not found for externalReference: " +externalReference));
	}

	@Override
	public PaymentQRCode generatePayment(String order, String uri, Long minuteToExpirations) {
		Payment payment = paymentGateway.findById(order).orElseThrow(() ->
				new NotFoundException("Payment not found for send with order: "+order));
		if(Optional.ofNullable(payment.getOrderStatus()).orElse("").equals("expired")){
			throw new BusinessException("Payment expired to order: "+ order);
		}
		payment.setNotificationUrl(uri);
		payment.setExpirationDate(LocalDateTime.now().plusMinutes(minuteToExpirations));
		save(payment);
		return paymentExternalGateway.sendPayment(payment)
				.orElseThrow(() -> new BusinessException("Fail to get QR code from mercado livre"));
	}

	@Override
	public void webhook(String resource) {
		isNullOrEmpty(resource);
		PaymentResponseML paymentMl = paymentExternalGateway.checkPayment(resource);
		Payment payment = paymentGateway.findById(paymentMl.externalReference())
				.orElseThrow(() -> new NotFoundException("Payment not found for externalReference: "
						+ paymentMl.externalReference()));
		payment.changeStatus(paymentMl.orderStatus());
		paymentGateway.insert(payment);
	}

	@Override
	public List<Payment> findNotSendAndIsPaid() {
		return paymentGateway.findNotSendAndIsPaid();
	}

	@Override
	public List<Payment> findPaymentExpired(LocalDateTime now) {
		return paymentGateway.findPaymentExpired(now);
	}

	@Override
	public void create(Payment payment) {
		paymentGateway.insert(payment);
	}

	private void isNullOrEmpty(String resource) {
		if(Optional.ofNullable(resource).orElse("").isEmpty()){
			throw new BusinessException("Resource can't be null!");
		}
	}
}
