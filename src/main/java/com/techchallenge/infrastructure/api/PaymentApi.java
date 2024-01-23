package com.techchallenge.infrastructure.api;


import com.techchallenge.application.usecase.PaymentUseCase;
import com.techchallenge.core.response.Result;
import com.techchallenge.domain.entity.Payment;
import com.techchallenge.domain.entity.PaymentQRCode;
import com.techchallenge.infrastructure.api.mapper.PaymentMapper;
import com.techchallenge.infrastructure.api.request.PayRequest;
import com.techchallenge.infrastructure.api.request.PaymentResponse;
import com.techchallenge.infrastructure.api.request.PaymentWebhookRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("api/v1/payment")
public class PaymentApi {

	private PaymentUseCase paymentUseCase;
	private PaymentMapper mapper;

	@Value("${notification.url}")
	private String notificationUrl;

	public PaymentApi(PaymentUseCase paymentUseCase, PaymentMapper mapper) {
		super();
		this.paymentUseCase = paymentUseCase;
		this.mapper = mapper;
	}



	@PostMapping("/pay")
	public ResponseEntity<InputStreamResource> checkout(@RequestBody PayRequest payRequest, UriComponentsBuilder uri )  {
		UriComponents uriComponents = uri.path("/api/v1/payment/webhook").build();
		String notification = uriComponents.toUriString();
		if(StringUtils.isNotBlank(notificationUrl)){
			notification = notificationUrl;
		}
		PaymentQRCode qrCode = paymentUseCase.generatePayment(payRequest.externalReference(), notification);
		return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(new InputStreamResource(qrCode.getQrCode()));
	}

	@PostMapping("/webhook")
	public ResponseEntity<Result<String>> webhook(@RequestBody PaymentWebhookRequest request) {
		paymentUseCase.webhook(request.resource());
		return ResponseEntity.ok(Result.ok("Webhook process with success!"));

	}

	@GetMapping("/find/{externalReference}")
	public ResponseEntity<Result<PaymentResponse>> findbyExternalReference(@PathVariable String externalReference) {
		Payment payment = paymentUseCase.findByExternalReference(externalReference);
		return ResponseEntity.ok(Result.ok(mapper.toPaymentResponse(payment)));

	}

}
