package com.techchallenge.infrastructure.message.consumer.dto;


import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(String orderId, CustomerDto customer, LocalDateTime dateOrderInit,
					   LocalDateTime dateOrderFinish,
					   List<ProductDto> products, String statusOrder ) {


}
