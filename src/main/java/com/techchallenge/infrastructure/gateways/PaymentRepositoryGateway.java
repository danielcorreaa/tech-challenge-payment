package com.techchallenge.infrastructure.gateways;


import com.techchallenge.application.gateway.PaymentGateway;
import com.techchallenge.domain.entity.Payment;
import com.techchallenge.infrastructure.persistence.documents.PaymentDocument;
import com.techchallenge.infrastructure.persistence.mapper.PaymentDocumentMapper;
import com.techchallenge.infrastructure.persistence.repository.PaymentCollection;
import com.techchallenge.infrastructure.persistence.repository.PaymentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class PaymentRepositoryGateway implements PaymentGateway {

	private PaymentRepository paymentRepository;
	private PaymentDocumentMapper mapper;
	private final PaymentCollection paymentCollection;
	public PaymentRepositoryGateway(PaymentRepository paymentRepository, PaymentDocumentMapper mapper, PaymentCollection paymentCollection) {
		super();
		this.paymentRepository = paymentRepository;
		this.mapper = mapper;
		this.paymentCollection = paymentCollection;
	}

	@Transactional
	@Override
	public Payment insert(Payment payment) {
		PaymentDocument paymentDocument = paymentRepository.save(mapper.toPaymentDocument(payment));
		return mapper.toPayment(paymentDocument);
	}

	@Override
	public Optional<Payment> findById(String order) {
		var paymentDocument = paymentRepository.findById(order);
		return paymentDocument.map( payment -> mapper.toPayment(payment));
	}

	@Override
	public List<Payment> findNotSendAndIsPaid() {
		List<PaymentDocument> payments =  paymentRepository
				.findNotSendAndIsPaid(Sort.by(Sort.Direction.ASC, "createTime"));
		return mapper.toPayments(payments);
	}

	@Override
	public List<Payment> findPaymentExpired(LocalDateTime now) {
		List<PaymentDocument> payments = paymentCollection.findPaymentExpired(now);
		return mapper.toPayments(payments);
	}


}
