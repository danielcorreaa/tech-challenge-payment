package com.techchallenge.application.usecase.interactor;

;
import com.techchallenge.application.gateway.PaymentExternalGateway;
import com.techchallenge.application.gateway.PaymentGateway;
import com.techchallenge.application.usecase.PaymentUseCase;
import com.techchallenge.core.exceptions.NotFoundException;
import com.techchallenge.domain.entity.Payment;
import com.techchallenge.domain.entity.PaymentQRCode;
import com.techchallenge.infrastructure.external.dtos.PaymentResponseML;

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
	public PaymentQRCode generatePayment(String order, String uri) {
		Payment payment = paymentGateway.findById(order).orElseThrow(() ->
				new NotFoundException("Payment not found for send with order: "+order));
		if(Optional.ofNullable(payment.getNotificationUrl()).isEmpty()){
			payment.setNotificationUrl(uri);
		}
		PaymentQRCode paymentQRCode =  paymentExternalGateway.sendPayment(payment);
		return paymentQRCode;
	}

	@Override
	public void webhook(String resource) {
		PaymentResponseML paymentMl = paymentExternalGateway.checkPayment(resource);
		Payment payment = paymentGateway.findById(paymentMl.externalReference())
				.orElseThrow(() -> new NotFoundException("Payment not found for externalReference: "
						+ paymentMl.externalReference()));
		payment.changeStatus(paymentMl.orderStatus());
		paymentGateway.insert(payment);
	}
}
