package com.techchallenge.infrastructure.api.mapper;

import com.techchallenge.domain.entity.Payment;
import com.techchallenge.domain.valueobject.Item;
import com.techchallenge.infrastructure.api.request.ItemsRequest;
import com.techchallenge.infrastructure.api.request.PaymentRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentMapper {

	public Payment toPayment(PaymentRequest request) {
		return new Payment(request.externalReference(),
				request.title(), request.description(), request.notificationUrl(),
				toItems(request.items()));
	}
	public Item toItem(ItemsRequest itemsRequest){
		return new Item(itemsRequest.skuNumber(),
					itemsRequest.category(),
					itemsRequest.title(),
					itemsRequest.description(),
					itemsRequest.unitPrice(),
					itemsRequest.quantity());
	}

	public List<Item> toItems(List<ItemsRequest> items){
		return items.stream().map(item -> toItem(item)).toList();
	}
}
