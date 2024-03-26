package com.techchallenge.infrastructure.api;


import com.techchallenge.application.usecase.PaymentUseCase;
import com.techchallenge.core.response.Result;
import com.techchallenge.domain.entity.Payment;
import com.techchallenge.domain.entity.PaymentQRCode;
import com.techchallenge.infrastructure.api.mapper.PaymentMapper;
import com.techchallenge.infrastructure.api.request.PayRequest;
import com.techchallenge.infrastructure.api.request.PaymentRequest;
import com.techchallenge.infrastructure.api.request.PaymentResponse;
import com.techchallenge.infrastructure.api.request.PaymentWebhookRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Log4j2
@RestController
@RequestMapping("payment/api/v1")
public class PaymentApi {

	private PaymentUseCase paymentUseCase;
	private PaymentMapper mapper;

	@Value("${notification.url}")
	private String notificationUrl;

	@Value("${time.expiration.payment}")
	private Long minuteToExpirations;

	public PaymentApi(PaymentUseCase paymentUseCase, PaymentMapper mapper) {
		super();
		this.paymentUseCase = paymentUseCase;
		this.mapper = mapper;
	}

	@PostMapping("/create")
	public ResponseEntity<Result<String>> checkout(@RequestBody PaymentRequest request, UriComponentsBuilder uri ) throws IOException {
		paymentUseCase.create(mapper.toPayment(request));
		return ResponseEntity.status(HttpStatus.CREATED).body(Result.create("Payment create with success!"));
	}

	@PostMapping("/pay")
	public ResponseEntity<InputStreamResource> checkout(@RequestBody PayRequest payRequest, UriComponentsBuilder uri )  {
		UriComponents uriComponents = uri.path("/api/v1/payment/webhook").build();
		String notification = uriComponents.toUriString();
		if(StringUtils.isNotBlank(notificationUrl)){
			notification = notificationUrl;
		}
		PaymentQRCode qrCode = paymentUseCase.generatePayment(payRequest.externalReference(), notification, minuteToExpirations);
		return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(new InputStreamResource(qrCode.getQrCode()));
	}

	@PostMapping("/webhook")
	public ResponseEntity<Result<String>> webhook(@RequestBody PaymentWebhookRequest request) {
		log.info(request);
		paymentUseCase.webhook(request.resource());
		var result = Result.ok("Webhook process with success!");
		return ResponseEntity.status(HttpStatus.OK).headers(result.getHeadersNosniff()).body(result);

	}

	@GetMapping("/find/{externalReference}")
	public ResponseEntity<Result<PaymentResponse>> findbyExternalReference(@PathVariable String externalReference) {
		Payment payment = paymentUseCase.findByExternalReference(externalReference);
		return ResponseEntity.ok(Result.ok(mapper.toPaymentResponse(payment)));

	}

}
