package com.techchallenge.infrastructure.message.consumer;


import com.techchallenge.KafkaTestConfig;

import com.techchallenge.application.usecase.PaymentUseCase;
import com.techchallenge.core.kafka.KafkaProducerConfig;
import com.techchallenge.core.response.JsonUtils;
import com.techchallenge.core.response.ObjectMapperConfig;
import com.techchallenge.core.utils.FileUtils;
import com.techchallenge.domain.entity.Payment;

import com.techchallenge.infrastructure.message.consumer.dto.OrderDto;
import com.techchallenge.util.ObjectMock;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration( classes = {KafkaTestConfig.class})
@TestPropertySource(locations = {"classpath:application-test.properties"})
@Testcontainers
class OrderConsumerIntegrationTest {


    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:6.0.2"))
            .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(20)));
    @Container
    static KafkaContainer kafkaContainer =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));
    @DynamicPropertySource
    static void overrrideMongoDBContainerProperties(DynamicPropertyRegistry registry){
        registry.add("spring.data.mongodb.host", mongoDBContainer::getHost);
        registry.add("spring.data.mongodb.port", mongoDBContainer::getFirstMappedPort);

        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);

    }
    @BeforeAll
    static void setUp(){
        mongoDBContainer.withReuse(true);
        mongoDBContainer.start();
        kafkaContainer.withReuse(true);
        kafkaContainer.start();
    }
    @AfterAll
    static void setDown(){
        mongoDBContainer.stop();
        kafkaContainer.stop();
    }


    @Autowired
    OrderConsumer orderConsumer;

    @Autowired
    PaymentUseCase paymentUseCase;

    @Autowired
    KafkaProducerConfig produce;

    String title = "Lanchonete Tech Challenge";

    String description = "Lanchonete Tech Challenge, Lanches, Bebidas, Sobremesas";

    ObjectMock mock;

    JsonUtils jsonUtils;

    @Value(value = "${kafka.topic.consumer.orders}")
    String topic;

    @BeforeEach
    void init(){
        jsonUtils = new JsonUtils(new ObjectMapperConfig().objectMapper());
        mock = new ObjectMock();
        ReflectionTestUtils.setField(orderConsumer, "title", "Lanchonete Tech Challenge");
        ReflectionTestUtils.setField(orderConsumer, "description", "Lanchonete Tech Challenge, Lanches, Bebidas, Sobremesas");
    }

    @Test
    void testListenOrdersAndCreatePayment() throws InterruptedException {
        OrderDto orderDto = jsonUtils.parse(new FileUtils().getFile("/data/order.json"), OrderDto.class).get();
        kafkaProducer().kafkaTemplate().send(topic, orderDto);
        boolean messageConsumed = orderConsumer.getLatch().await(10, TimeUnit.SECONDS);
        Payment byExternalReference = paymentUseCase.findByExternalReference("6593732dcfdb826a875770ff");

        assertEquals("6593732dcfdb826a875770ff",byExternalReference.getExternalReference());

        assertTrue(messageConsumed);
    }

    @Test
    void testListenOrders_failedToSave() throws InterruptedException {
        OrderDto orderDto = jsonUtils.parse(new FileUtils().getFile("/data/orderError.json"), OrderDto.class).get();
        kafkaProducer().kafkaTemplate().send(topic, orderDto);
        boolean messageConsumed = orderConsumer.getLatch().await(10, TimeUnit.SECONDS);
        assertFalse(messageConsumed);
    }


    public KafkaProducerConfig kafkaProducer(){
        return new KafkaProducerConfig(kafkaContainer.getBootstrapServers());
    }


    public ProducerFactory<String, OrderDto> producerFactory(){
        return kafkaProducer().producerFactory();
    }

}