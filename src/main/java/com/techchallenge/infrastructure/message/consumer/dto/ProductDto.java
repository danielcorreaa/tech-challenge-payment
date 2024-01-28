package com.techchallenge.infrastructure.message.consumer.dto;


import java.math.BigDecimal;

public record ProductDto(String sku, String title, String category, String description, BigDecimal price, String image) {

}
