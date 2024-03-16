package com.techchallenge;

import com.techchallenge.config.KafkaConfig;
import com.techchallenge.core.kafka.produce.TopicProducer;
import com.techchallenge.domain.entity.MessagePayment;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import static org.mockito.Mockito.mock;


@ComponentScan(value = {"com.techchallenge"})
@EnableMongoRepositories
public class KafkaTestConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${kafka.topic.consumer.groupId}")
    private String groupId;




}
