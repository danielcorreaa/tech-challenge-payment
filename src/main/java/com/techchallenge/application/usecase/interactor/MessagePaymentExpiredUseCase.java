package com.techchallenge.application.usecase.interactor;

import com.techchallenge.application.usecase.MessageUseCase;
import com.techchallenge.core.kafka.produce.TopicProducer;
import com.techchallenge.domain.entity.MessagePayment;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.support.SendResult;

public class MessagePaymentExpiredUseCase implements MessageUseCase {

    @Qualifier(value = "payment-error")
    private TopicProducer<MessagePayment> topicProducerError;

    public MessagePaymentExpiredUseCase(TopicProducer<MessagePayment> topicProducerError) {
        this.topicProducerError = topicProducerError;
    }

    @Override
    public SendResult<String, MessagePayment> send(MessagePayment messagePayment) {
        return topicProducerError.produce(messagePayment.getExternalReference(), messagePayment);
    }
}
