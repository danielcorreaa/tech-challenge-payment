package com.techchallenge.infrastructure.gateways;


import com.techchallenge.application.gateway.MessageGateway;
import com.techchallenge.domain.entity.MessagePayment;
import com.techchallenge.domain.entity.Payment;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Log4j2
@Component
public class MessageGatewayInteractor implements MessageGateway {

    private KafkaTemplate<String, MessagePayment> kafkaTemplate;
    @Value(value = "${kafka.topic.producer.payment}")
    private String topic;

    public MessageGatewayInteractor(KafkaTemplate<String, MessagePayment> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(MessagePayment message) {
        CompletableFuture<SendResult<String, MessagePayment>> future = kafkaTemplate.send(topic, message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info(result);
            }
            else {
                log.error(ex.getClass().getSimpleName() + "(" + ex.getMessage() + ")");
            }
        });
    }
}
