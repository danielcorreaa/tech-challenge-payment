package com.techchallenge.infrastructure.message.consumer.dto;

import java.util.List;

public record OrderDto(String orderId, CustomerDto customer, List<ProductDto> products ) {


}
