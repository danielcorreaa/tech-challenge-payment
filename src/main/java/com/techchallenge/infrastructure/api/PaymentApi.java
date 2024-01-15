package com.techchallenge.infrastructure.api;


import com.techchallenge.application.usecase.PaymentUseCase;
import com.techchallenge.core.response.Result;
import com.techchallenge.domain.entity.Payment;
import com.techchallenge.domain.entity.PaymentQRCode;
import com.techchallenge.infrastructure.api.mapper.PaymentMapper;
import com.techchallenge.infrastructure.api.request.PayRequest;
import com.techchallenge.infrastructure.api.request.PaymentResponse;
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



	@PostMapping("/pay")
	public ResponseEntity<InputStreamResource> checkout(@RequestBody PayRequest payRequest, UriComponentsBuilder uri ) throws IOException {
		UriComponents uriComponents = uri.path("/api/v1/payment/webhook").build();
		PaymentQRCode qrCode = paymentUseCase.generatePayment(payRequest.externalReference(), uriComponents.toUriString());
		return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(new InputStreamResource(qrCode.getQrCode()));
	}

	@PostMapping("/webhook")
	public ResponseEntity<Result<String>> webhook(@RequestBody PaymentWebhookRequest request) throws IOException {
		paymentUseCase.webhook(request.resource());
		return ResponseEntity.ok(Result.ok("Webhook process with success!"));

	}

	@GetMapping("/find/{externalReference}")
	public ResponseEntity<Result<PaymentResponse>> findbyExternalReference(@PathVariable String externalReference) throws IOException {
		Payment payment = paymentUseCase.findByExternalReference(externalReference);
		return ResponseEntity.ok(Result.ok(mapper.toPaymentResponse(payment)));

	}

}
