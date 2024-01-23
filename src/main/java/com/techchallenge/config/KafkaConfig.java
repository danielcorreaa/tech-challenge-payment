package com.techchallenge.config;

import com.techchallenge.application.gateway.PaymentGateway;
import com.techchallenge.application.usecase.MessageUseCase;
import com.techchallenge.application.usecase.PaymentUseCase;
import com.techchallenge.application.usecase.interactor.MessageUseCaseInteractor;
import com.techchallenge.core.kafka.KafkaConsumerConfig;
import com.techchallenge.core.kafka.KafkaProducerConfig;
import com.techchallenge.core.kafka.produce.TopicProducer;
import com.techchallenge.domain.entity.MessagePayment;
import com.techchallenge.infrastructure.message.consumer.OrderConsumer;
import com.techchallenge.infrastructure.message.consumer.dto.OrderDto;
import com.techchallenge.infrastructure.message.consumer.mapper.OrderMessageMapper;
import com.techchallenge.infrastructure.message.produce.PaymentProduce;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${kafka.topic.producer.payment}")
    private String topic;

    @Value(value = "${kafka.topic.consumer.groupId}")
    private String groupId;

    @Bean
    public KafkaConsumerConfig kafkaConsumer(){
        return new KafkaConsumerConfig(bootstrapAddress, groupId);
    }

    @Bean
    public ConsumerFactory<String, OrderDto> consumerFactoryOrderDto(){
        return kafkaConsumer().consumerFactory(jsonDeserializer(new JsonDeserializer<>(OrderDto.class)));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderDto> kafkaListenerContainerFactoryOrderDto(){
        return kafkaConsumer().kafkaListenerContainerFactory(consumerFactoryOrderDto());
    }

    @Bean
    public KafkaProducerConfig kafkaProducer(){
        return new KafkaProducerConfig(bootstrapAddress);
    }

    @Bean
    public ProducerFactory<String, MessagePayment> producerFactory(){
        return kafkaProducer().producerFactory();
    }

    @Bean
    public KafkaTemplate<String, MessagePayment> kafkaTemplate() {
        return kafkaProducer().kafkaTemplate();
    }

    @Bean
    public OrderConsumer orderConsumer(PaymentUseCase paymentUseCase, OrderMessageMapper mapper){
        return new OrderConsumer(paymentUseCase,mapper);
    }

    @Bean
    public TopicProducer<MessagePayment> topicProducer(){
        return new TopicProducer<>(kafkaTemplate(), topic);
    }

    @Bean
    public MessageUseCase messageUseCase(PaymentGateway paymentGateway){
        return  new MessageUseCaseInteractor(topicProducer());
    }

    public <T> JsonDeserializer<T> jsonDeserializer(JsonDeserializer<T> deserializer){
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }
}
