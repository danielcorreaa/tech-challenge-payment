package com.techchallenge.infrastructure.gateways;

import com.techchallenge.application.gateway.PaymentExternalGateway;
import com.techchallenge.core.exceptions.BusinessException;
import com.techchallenge.domain.entity.Payment;
import com.techchallenge.domain.entity.PaymentQRCode;
import com.techchallenge.infrastructure.external.HttpRequestML;
import com.techchallenge.infrastructure.external.QRCodeGenerator;
import com.techchallenge.infrastructure.external.dtos.OrderResponseML;
import com.techchallenge.infrastructure.external.dtos.PaymentResponseML;
import com.techchallenge.infrastructure.external.mapper.OrderMLMapper;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

@Component
public class PaymentIntegrationMLGateway implements PaymentExternalGateway {

	private HttpRequestML httpRequestML;
	private OrderMLMapper mapper;
	@Value("${api.mercadolivre.token}")
	private String token;

	public PaymentIntegrationMLGateway(HttpRequestML httpRequestML, OrderMLMapper mapper) {
		this.httpRequestML = httpRequestML;
		this.mapper = mapper;
	}

	@Override
	public Optional<PaymentQRCode> sendPayment(Payment payment) {
		Optional<OrderResponseML> response = getOrderResponseML(payment);
		if(response.isPresent()) {
			try {
				byte[] qrCodeImage = QRCodeGenerator.getQRCodeImage(response.get().QrData(), 200, 200);
				InputStream in = new ByteArrayInputStream(qrCodeImage);
				return Optional.of(new PaymentQRCode(in));
			} catch (Exception e) {
				throw new BusinessException("Fail to generate QR Code", e);
			}
		}
		return Optional.empty();
	}

	private Optional<OrderResponseML> getOrderResponseML(Payment payment) {
		OrderResponseML response = null;
		try {
			response = httpRequestML.sendOrderToMl("Bearer " + token,
					mapper.toOrdersML(payment));
			return Optional.ofNullable(response);
		} catch (FeignException fe){
			throw new BusinessException("Error to sendPayment for Mercado Pago", fe);
		}
	}

	@Override
	public PaymentResponseML checkPayment(String resource) {
		String[] split = resource.split("/");
		String param = split[split.length - 1];
		PaymentResponseML payment = null;
		try{
			return httpRequestML.findPayment("Bearer " + token, param);
		} catch (FeignException fe){
			throw new BusinessException("Error to check payment in Mercado Pago!");
		}
	}
}
