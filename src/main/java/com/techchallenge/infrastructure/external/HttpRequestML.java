package com.techchallenge.infrastructure.external;

import com.techchallenge.infrastructure.external.dtos.OrderResponseML;
import com.techchallenge.infrastructure.external.dtos.OrdersML;
import com.techchallenge.infrastructure.external.dtos.PaymentResponseML;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "orders", url = "${api.mercadolivre.url}")
public interface HttpRequestML {
	
	@PostMapping("/${api.mercadolivre.orders}")
	OrderResponseML sendOrderToMl(@RequestHeader("Authorization") String bearerToken, OrdersML orders);

	@GetMapping(path = "/${api.mercadolivre.payment}/{param}")
	PaymentResponseML findPayment(@RequestHeader("Authorization") String bearerToken, @PathVariable String param);
	
}
