package com.techchallenge.infrastructure.external;

import com.techchallenge.infrastructure.external.dtos.PaymentResponseML;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "payment", url = "${api.mercadolivre.payment}")
public interface MercadoLivrePayment {	
	
	@GetMapping(path = "/{param}")
	PaymentResponseML findPayment(@RequestHeader("Authorization") String bearerToken, @PathVariable String param);
}
