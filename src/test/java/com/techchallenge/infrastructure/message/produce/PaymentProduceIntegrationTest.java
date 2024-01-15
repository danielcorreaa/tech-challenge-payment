package com.techchallenge.infrastructure.message.produce;

import com.techchallenge.KafkaTestConfig;
import com.techchallenge.application.usecase.MessageUseCase;
import com.techchallenge.application.usecase.PaymentUseCase;
import com.techchallenge.domain.entity.Payment;
import com.techchallenge.infrastructure.persistence.mapper.PaymentDocumentMapper;
import com.techchallenge.infrastructure.persistence.repository.PaymentRepository;
import com.techchallenge.util.ObjectMock;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;


@ExtendWith(SpringExtension.class)
@ContextConfiguration( classes = {KafkaTestConfig.class})
@TestPropertySource(locations = {"classpath:application-test.properties"})
@Testcontainers
class PaymentProduceIntegrationTest {

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


    ObjectMock mock;

    @Autowired
    PaymentProduce paymentProduce;

    @Autowired
    PaymentUseCase paymentUseCase;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    PaymentDocumentMapper paymentDocumentMapper;

    @Autowired
    private MessageUseCase messageUseCase;

    @BeforeEach
    void init(){
        mock = new ObjectMock();
        clearDb();
    }



    @Test
    void testProcessMessageWithSuccess(){
        insertPayments();

        List<Payment> notSendAndIsPaid = paymentUseCase.findNotSendAndIsPaid();
        Assertions.assertEquals(5, notSendAndIsPaid.size());

        paymentProduce.process();

        notSendAndIsPaid = paymentUseCase.findNotSendAndIsPaid();
        Assertions.assertEquals(0, notSendAndIsPaid.size());
    }

    @Test
    void testProcessMessageNoMessageToProduce(){

        List<Payment> notSendAndIsPaid = paymentUseCase.findNotSendAndIsPaid();
        Assertions.assertEquals(0, notSendAndIsPaid.size());

        paymentProduce.process();

        notSendAndIsPaid = paymentUseCase.findNotSendAndIsPaid();
        Assertions.assertEquals(0, notSendAndIsPaid.size());
    }

    void insertPayments(){
        List<Payment> payments =  mock.getPaymentsToPaid();
        payments.forEach(paymentUseCase::save);
    }

    void clearDb() {
        paymentRepository.deleteAll();
    }

}