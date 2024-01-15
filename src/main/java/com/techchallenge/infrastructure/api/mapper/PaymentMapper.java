package com.techchallenge.infrastructure.api.mapper;

import com.techchallenge.domain.entity.Payment;
import com.techchallenge.domain.valueobject.Item;
import com.techchallenge.infrastructure.api.request.ItemResponse;
import com.techchallenge.infrastructure.api.request.ItemsRequest;
import com.techchallenge.infrastructure.api.request.PaymentRequest;
import com.techchallenge.infrastructure.api.request.PaymentResponse;
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

	public PaymentResponse toPaymentResponse(Payment payment) {
		return PaymentResponse.builder()
				.sent(payment.getSent())
				.createTime(payment.getCreateTime())
				.orderStatus(payment.getOrderStatus())
				.externalReference(payment.getExternalReference())
				.totalAmount(payment.getTotalAmount())
				.items(toItemsResponse(payment.getItems()))
				.notificationUrl(payment.getNotificationUrl())
				.description(payment.getDescription())
				.title(payment.getTitle()).build();
	}


	private ItemResponse toItemResponse(Item item) {
		return  ItemResponse.builder()
				.unitMeasure(item.getUnitMeasure())
				.totalAmount(item.getTotalAmount())
				.title(item.getTitle())
				.description(item.getDescription())
				.skuNumber(item.getSkuNumber())
				.unitPrice(item.getUnitPrice())
				.quantity(item.getQuantity())
				.category(item.getCategory())
				.build();
	}
	private List<ItemResponse> toItemsResponse(List<Item> items) {
		return items.stream().map(item -> toItemResponse(item)).toList();
	}
}
