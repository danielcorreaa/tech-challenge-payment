package com.techchallenge.infrastructure.message.consumer.mapper;

import com.techchallenge.domain.entity.Payment;
import com.techchallenge.domain.valueobject.Item;
import com.techchallenge.infrastructure.message.consumer.dto.OrderDto;
import com.techchallenge.infrastructure.message.consumer.dto.ProductDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMessageMapper {
    public Payment toPayment(String title, String description, OrderDto orderDto) {
        return new Payment(orderDto.orderId(), title, description,null,
                toItems(orderDto.products()), orderDto.cpf());
    }
    public Item toItem(ProductDto productDto){
        return new Item(productDto.sku(),
                productDto.category(),
                productDto.title(),
                productDto.description(),
                productDto.price(),
                1);
    }

    public List<Item> toItems(List<ProductDto> productDtos){
        return productDtos.stream().map(this::toItem).toList();
    }
}
