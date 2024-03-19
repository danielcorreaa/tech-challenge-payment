package com.techchallenge;

import com.techchallenge.application.gateway.PaymentGateway;
import com.techchallenge.application.usecase.MessageUseCase;
import com.techchallenge.application.usecase.interactor.MessageUseCaseInteractor;
import com.techchallenge.config.KafkaConfig;
import com.techchallenge.core.kafka.produce.TopicProducer;
import com.techchallenge.domain.entity.MessagePayment;
import com.techchallenge.infrastructure.message.produce.PaymentProduce;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.mock;


@ComponentScan(value = {"com.techchallenge"})
@EnableMongoRepositories
public class MongoTestConfig {


    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${kafka.topic.consumer.groupId}")
    private String groupId;

    @Primary
    @Bean
    public KafkaConfig kafkaConfig(){
        return mock(KafkaConfig.class);
    }

    @Primary
    @Bean
    public MessageUseCase messageUseCase(PaymentGateway paymentGateway){
        return mock(MessageUseCaseInteractor.class);
    }


    @Primary
    @Bean(name = "payment-success")
    public TopicProducer<MessagePayment> topicProducer(){
        return mock(TopicProducer.class);
    }

    @Primary
    @Bean(name = "payment-error")
    public TopicProducer<MessagePayment> topicProducerError(){
        return mock(TopicProducer.class);

    }

    @Primary
    @Bean
    public PaymentProduce paymentProduce(){
        return mock(PaymentProduce.class);
    }

}
