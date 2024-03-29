package com.techchallenge.infrastructure.persistence.mapper;

import com.techchallenge.domain.entity.Payment;
import com.techchallenge.domain.valueobject.Item;
import com.techchallenge.infrastructure.persistence.documents.ItemDocument;
import com.techchallenge.infrastructure.persistence.documents.PaymentDocument;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentDocumentMapper {

    public Payment toPayment(PaymentDocument paymentDocument) {
        return Payment.toPayment(paymentDocument.getId(),
                paymentDocument.getTitle(),
                paymentDocument.getDescription(),
                paymentDocument.getNotificationUrl(),
                paymentDocument.getOrderStatus(),
                toItems(paymentDocument.getItems()),
                paymentDocument.getCreateTime(),
                paymentDocument.getSent(),
                paymentDocument.getExpirationDate(),
                paymentDocument.getCpfCustomer()
                );
    }

    public Item toItem(ItemDocument itemDocument){
        return new Item(itemDocument.getSkuNumber(),
                itemDocument.getCategory(),
                itemDocument.getTitle(),
                itemDocument.getDescription(),
                itemDocument.getUnitPrice(),
                itemDocument.getQuantity());
    }

    public List<Item> toItems(List<ItemDocument> items){
        return items.stream().map(this::toItem).toList();
    }

    public PaymentDocument toPaymentDocument(Payment payment) {
        return PaymentDocument.builder().title(payment.getTitle())
                .description(payment.getDescription())
                .id(payment.getExternalReference())
                .notificationUrl(payment.getNotificationUrl())
                .totalAmount(payment.getTotalAmount())
                .items(toItemsDocuments(payment.getItems()))
                .orderStatus(payment.getOrderStatus())
                .sent(payment.getSent())
                .createTime(payment.getCreateTime())
                .id(payment.getExternalReference())
                .expirationDate(payment.getExpirationDate())
                .cpfCustomer(payment.getCpfCustomer().orElse(""))
                .build();
    }

    public ItemDocument toItemDocument(Item item){
        return ItemDocument.builder()
                .category(item.getCategory())
                .quantity(item.getQuantity())
                .skuNumber(item.getSkuNumber())
                .title(item.getTitle())
                .description(item.getDescription())
                .unitMeasure(item.getUnitMeasure())
                .unitPrice(item.getUnitPrice())
                .totalAmount(item.getTotalAmount()).build();
    }
    public List<ItemDocument> toItemsDocuments(List<Item> items){
        return items.stream().map(this::toItemDocument).toList();
    }

    public List<Payment> toPayments(List<PaymentDocument> payments) {
        return payments.stream().map(this::toPayment).toList();
    }
}
