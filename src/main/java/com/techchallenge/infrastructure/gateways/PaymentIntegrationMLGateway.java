package com.techchallenge.infrastructure.gateways;

import com.techchallenge.application.gateway.PaymentExternalGateway;
import com.techchallenge.core.exceptions.BusinessException;
import com.techchallenge.domain.entity.Payment;
import com.techchallenge.domain.entity.PaymentQRCode;
import com.techchallenge.infrastructure.external.HttpRequestML;
import com.techchallenge.infrastructure.external.MercadoLivrePayment;
import com.techchallenge.infrastructure.external.QRCodeGenerator;
import com.techchallenge.infrastructure.external.dtos.OrderResponseML;
import com.techchallenge.infrastructure.external.dtos.PaymentResponseML;
import com.techchallenge.infrastructure.external.mapper.OrderMLMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Component
public class PaymentIntegrationMLGateway implements PaymentExternalGateway {

	private HttpRequestML httpRequestML;

	private MercadoLivrePayment mercadoLivrePayment;
	private OrderMLMapper mapper;
	@Value("${api.mercadolivre.token}")
	private String token;

	public PaymentIntegrationMLGateway(HttpRequestML httpRequestML, OrderMLMapper mapper, MercadoLivrePayment mercadoLivrePayment) {
		this.httpRequestML = httpRequestML;
		this.mapper = mapper;
		this.mercadoLivrePayment = mercadoLivrePayment;
	}

	@Override
	public PaymentQRCode sendPayment(Payment payment) {
		OrderResponseML response = httpRequestML.sendOrderToMl("Bearer " + token,
				mapper.toOrdersML(payment));
		byte[] qrCodeImage = null;
		try {
			qrCodeImage = QRCodeGenerator.getQRCodeImage(response.QrData(), 200, 200);
		} catch (Exception e) {
			throw new BusinessException("Fail to generate QR Code", e);
		}
		InputStream in = new ByteArrayInputStream(qrCodeImage);
		return new PaymentQRCode(in);
	}

	@Override
	public PaymentResponseML checkPayment(String resource) {
		String[] split = resource.split("/");
		String param = split[split.length - 1];
		return mercadoLivrePayment.findPayment("Bearer " + token, param);
	}
}
