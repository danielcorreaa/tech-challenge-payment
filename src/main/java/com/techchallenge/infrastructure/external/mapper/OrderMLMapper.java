package com.techchallenge.infrastructure.external.mapper;

import com.techchallenge.domain.entity.Payment;
import com.techchallenge.domain.valueobject.Item;
import com.techchallenge.infrastructure.external.dtos.ItemsML;
import com.techchallenge.infrastructure.external.dtos.OrdersML;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class OrderMLMapper {
    public OrdersML toOrdersML(Payment payment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String expirationDateString = payment.getExpirationDate().format(formatter) + "-03:00";
        return new OrdersML(payment.getExternalReference(),
                payment.getTitle(), payment.getDescription(), payment.getNotificationUrl(),
                payment.getTotalAmount(), expirationDateString, toItemsMl(payment.getItems()));
    }

    public ItemsML toItemMl(Item item){
        return new ItemsML(item.getSkuNumber(), item.getCategory(), item.getTitle(),
                item.getDescription(), item.getUnitPrice(), item.getQuantity(), item.getUnitMeasure(), item.getTotalAmount());
    }

    public List<ItemsML> toItemsMl(List<Item> items){
        return items.stream().map(this::toItemMl).toList();
    }


}
