package com.techchallenge.infrastructure.external;

import com.techchallenge.infrastructure.external.dtos.OrderResponseML;
import com.techchallenge.infrastructure.external.dtos.OrdersML;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "orders", url = "${api.mercadolivre.orders}")
public interface HttpRequestML {
	
	@PostMapping
	OrderResponseML sendOrderToMl(@RequestHeader("Authorization") String bearerToken, OrdersML orders);
	
}
