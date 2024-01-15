package com.techchallenge.infrastructure.consumer;

import com.techchallenge.application.usecase.PaymentUseCase;
import com.techchallenge.infrastructure.message.consumer.dto.OrderDto;
import com.techchallenge.infrastructure.message.consumer.mapper.OrderMessageMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.concurrent.CountDownLatch;

@Log4j2
public class OrderConsumer {

    @Value("${lanchonete.title}")
    private String title;

    @Value("${lanchonete.description}")
    private String description;

    private PaymentUseCase paymentUseCase;
    private OrderMessageMapper mapper;

    private CountDownLatch latch = new CountDownLatch(1);

    public OrderConsumer(PaymentUseCase paymentUseCase, OrderMessageMapper mapper) {
        this.paymentUseCase = paymentUseCase;
        this.mapper = mapper;
    }

    @KafkaListener(topics = "${kafka.topic.consumer.orders}", groupId = "${kafka.topic.consumer.groupId}",
            containerFactory = "kafkaListenerContainerFactoryOrderDto")
    public void listenOrders(@Payload OrderDto record, Acknowledgment ack) {
        log.info("Received Message: " + record.toString());
        try {
            paymentUseCase.save(mapper.toPayment(title, description, record));
            ack.acknowledge();
            latch.countDown();
        } catch (Exception ex){
            log.error("Message not save: "+ ex.getMessage());
        }
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
