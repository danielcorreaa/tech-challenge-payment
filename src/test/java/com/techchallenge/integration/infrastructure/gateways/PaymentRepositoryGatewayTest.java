package com.techchallenge.integration.infrastructure.gateways;

import com.techchallenge.MongoTestConfig;
import com.techchallenge.domain.entity.Payment;
import com.techchallenge.infrastructure.gateways.PaymentRepositoryGateway;
import com.techchallenge.infrastructure.persistence.mapper.PaymentDocumentMapper;
import com.techchallenge.infrastructure.persistence.repository.PaymentRepository;
import com.techchallenge.util.ObjectMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration( classes = {MongoTestConfig.class})
@TestPropertySource(locations = "classpath:/application-test.properties")
@Testcontainers
class PaymentRepositoryGatewayTest {


    PaymentRepositoryGateway paymentRepositoryGateway;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentDocumentMapper mapper;

    ObjectMock mock;


    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:6.0.2"))
            .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(20)));

    @DynamicPropertySource
    static void overrrideMongoDBContainerProperties(DynamicPropertyRegistry registry){
        registry.add("spring.data.mongodb.host", mongoDBContainer::getHost);
        registry.add("spring.data.mongodb.port", mongoDBContainer::getFirstMappedPort);
    }

    @BeforeAll
    static void setUp(){
        mongoDBContainer.withReuse(true);
        mongoDBContainer.start();
    }

    @AfterAll
    static void setDown(){
        mongoDBContainer.stop();
    }

    @BeforeEach
    void init(){
        paymentRepositoryGateway = new PaymentRepositoryGateway(paymentRepository, mapper);
        mock = new ObjectMock();
        clear();
    }

    @Test
    void testInsertPayment(){
        Payment payment = mock.getPaymentMock("85");
        Payment insert = paymentRepositoryGateway.insert(payment);
        assertEquals("85", insert.getExternalReference());
        assertFalse(insert.getSent());
        assertNotNull(insert.getCreateTime());
        assertNull(insert.getOrderStatus());
    }

    @Test
    void testUpdateStatusOrderToPaid(){
        Payment payment = mock.getPaymentMock("858");
        Payment insert = paymentRepositoryGateway.insert(payment);
        insert.changeStatus("paid");
        paymentRepositoryGateway.insert(insert);

        assertEquals("858", insert.getExternalReference());
        assertEquals("paid", insert.getOrderStatus());
        assertFalse(insert.getSent());
        assertNotNull(insert.getCreateTime());
    }


    @Test
    void testFindPayment_whenIsNotSent_and_OrderStatusIsPaid(){
        createListPaymentToTest();
        List<Payment> notSendAndIsPaid = paymentRepositoryGateway.findNotSendAndIsPaid();
        assertFalse(notSendAndIsPaid.stream().allMatch(Payment::getSent));
        assertTrue(notSendAndIsPaid.stream().allMatch(Payment::isPaid));
    }

    @Test
    void testFindPayment_whenNoResultFound(){
        List<Payment> notSendAndIsPaid = paymentRepositoryGateway.findNotSendAndIsPaid();
        assertTrue(notSendAndIsPaid.isEmpty());
    }

    @Test
    void testFindByOrderId_isNotPaid(){
        createListPaymentToTest();
        Optional<Payment> byId = paymentRepositoryGateway.findById("523006");
        assertEquals("523006", byId.get().getExternalReference());
        assertFalse(byId.get().isPaid());
    }

    @Test
    void testFindByOrderId_isPaid(){
        createListPaymentToTest();
        Optional<Payment> byId = paymentRepositoryGateway.findById("523001");
        assertEquals("523001", byId.get().getExternalReference());
        assertTrue(byId.get().isPaid());
    }

    @Test
    void testFindByOrderId_idNotFound(){
        createListPaymentToTest();
        Optional<Payment> payment =  paymentRepositoryGateway.findById("523008");
        assertFalse(payment.isPresent());
    }

    private void createListPaymentToTest() {
        List<Payment> paymentMock = mock.getPaymentsToPaid();

        paymentMock.forEach(pay -> paymentRepositoryGateway.insert(pay));

        Payment paymentNotPaid = mock.getPaymentMock("523006");
        paymentRepositoryGateway.insert(paymentNotPaid);

    }

    private void clear() {
        paymentRepository.deleteAll();
    }


}