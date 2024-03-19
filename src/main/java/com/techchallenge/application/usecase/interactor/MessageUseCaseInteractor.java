package com.techchallenge.application.usecase.interactor;

import com.techchallenge.application.usecase.MessageUseCase;
import com.techchallenge.core.kafka.produce.TopicProducer;
import com.techchallenge.domain.entity.MessagePayment;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.support.SendResult;

public class MessageUseCaseInteractor implements MessageUseCase {

    @Qualifier(value = "payment-success")
    private TopicProducer<MessagePayment> topicProducer;

    public MessageUseCaseInteractor(TopicProducer<MessagePayment> topicProducer) {
        this.topicProducer = topicProducer;
    }

    @Override
    public SendResult<String, MessagePayment> send(MessagePayment messagePayment) {
        return topicProducer.produce(messagePayment.getExternalReference(), messagePayment);
    }

}
