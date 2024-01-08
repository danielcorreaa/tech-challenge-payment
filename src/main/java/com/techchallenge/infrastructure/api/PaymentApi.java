package com.techchallenge.infrastructure.api;


import com.techchallenge.application.usecase.PaymentUseCase;
import com.techchallenge.core.response.Result;
import com.techchallenge.domain.entity.Payment;
import com.techchallenge.domain.entity.PaymentQRCode;
import com.techchallenge.infrastructure.api.mapper.PaymentMapper;
import com.techchallenge.infrastructure.api.request.PaymentWebhookRequest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/payment")
public class PaymentApi {

	private PaymentUseCase paymentUseCase;
	private PaymentMapper mapper;

	public PaymentApi(PaymentUseCase paymentUseCase, PaymentMapper mapper) {
		super();
		this.paymentUseCase = paymentUseCase;
		this.mapper = mapper;
	}



	@PostMapping("/pay/{external_reference}")
	public ResponseEntity<InputStreamResource> checkout(@PathVariable String external_reference, UriComponentsBuilder uri ) throws IOException {
		UriComponents uriComponents = uri.path("/api/v1/payment/webhook").build();
		PaymentQRCode qrCode = paymentUseCase.generatePayment(external_reference, uriComponents.toUriString());
		return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(new InputStreamResource(qrCode.getQrCode()));
	}

	@PostMapping("/webhook")
	public ResponseEntity<Result<String>> webhook(@RequestBody PaymentWebhookRequest request) throws IOException {
		paymentUseCase.webhook(request.resource());
		return ResponseEntity.ok(Result.ok("OK"));

	}

	@GetMapping("/find/order/{externalReference}")
	public ResponseEntity<Result<Payment>> findbyExternalReference(@PathVariable String externalReference) throws IOException {
		Payment payment = paymentUseCase.findByExternalReference(externalReference);
		return ResponseEntity.ok(Result.ok(payment));

	}

}
