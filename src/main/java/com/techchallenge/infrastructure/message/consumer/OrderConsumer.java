package com.techchallenge.infrastructure.consumer;

import com.techchallenge.application.usecase.PaymentUseCase;
import com.techchallenge.infrastructure.message.consumer.dto.OrderDto;
import com.techchallenge.infrastructure.message.consumer.mapper.OrderMessageMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

@Log4j2
public class OrderConsumer {

    @Value("${lanchonete.title}")
    private String title;

    @Value("${lanchonete.description}")
    private String description;

    private PaymentUseCase paymentUseCase;
    private OrderMessageMapper mapper;

    public OrderConsumer(PaymentUseCase paymentUseCase, OrderMessageMapper mapper) {
        this.paymentUseCase = paymentUseCase;
        this.mapper = mapper;
    }

    @KafkaListener(topics = "${kafka.topic.consumer.orders}", groupId = "${kafka.topic.consumer.groupId}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listenOrders(OrderDto message, Acknowledgment ack) {
        log.info("Received Message: " + message.toString());
        try {
            paymentUseCase.save(mapper.toPayment(title, description, message));
            ack.acknowledge();
        } catch (Exception ex){
            log.error("Message not save: "+ ex.getMessage());
        }
    }

}
