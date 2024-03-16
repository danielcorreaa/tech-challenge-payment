package com.techchallenge.application.usecase;

import com.techchallenge.MongoTestConfig;
import com.techchallenge.application.gateway.PaymentExternalGateway;
import com.techchallenge.application.gateway.PaymentGateway;
import com.techchallenge.application.usecase.interactor.PaymentUseCaseInteractor;
import com.techchallenge.core.exceptions.BusinessException;
import com.techchallenge.core.exceptions.NotFoundException;
import com.techchallenge.domain.entity.Payment;
import com.techchallenge.domain.entity.PaymentQRCode;
import com.techchallenge.infrastructure.external.HttpRequestML;
import com.techchallenge.infrastructure.external.dtos.OrderResponseML;
import com.techchallenge.infrastructure.external.dtos.OrdersML;
import com.techchallenge.infrastructure.external.dtos.PaymentResponseML;
import com.techchallenge.infrastructure.external.mapper.OrderMLMapper;
import com.techchallenge.infrastructure.gateways.PaymentIntegrationMLGateway;
import com.techchallenge.infrastructure.gateways.PaymentRepositoryGateway;
import com.techchallenge.infrastructure.persistence.mapper.PaymentDocumentMapper;
import com.techchallenge.infrastructure.persistence.repository.PaymentCollection;
import com.techchallenge.infrastructure.persistence.repository.PaymentRepository;
import com.techchallenge.util.PaymentHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration( classes = {MongoTestConfig.class})
@TestPropertySource(locations = "classpath:/application-test.properties")
@Testcontainers
class PaymentUseCaseIT {

    PaymentUseCase paymentUseCase;

    PaymentExternalGateway paymentExternalGateway;
    PaymentGateway paymentGateway;

    @Mock
    HttpRequestML httpRequestML;

    OrderMLMapper orderMLMapper;

    @Autowired
    PaymentRepository paymentRepository;
    PaymentDocumentMapper paymentDocumentMapper;
    PaymentHelper mock;

    private PaymentCollection paymentCollection;
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
        mock = new PaymentHelper();
        orderMLMapper = new OrderMLMapper();
        paymentExternalGateway = new PaymentIntegrationMLGateway(httpRequestML,orderMLMapper);
        ReflectionTestUtils.setField(paymentExternalGateway, "token", "test");

        paymentDocumentMapper = new PaymentDocumentMapper();
        paymentGateway = new PaymentRepositoryGateway(paymentRepository, paymentDocumentMapper, paymentCollection);

        paymentUseCase = new PaymentUseCaseInteractor(paymentExternalGateway, paymentGateway);

        clearDb();
        insertPaymentDB();
    }


    @Test
    void testCreatePayment() {
        Payment payment = mock.getPaymentMock("852369");
        paymentUseCase.save(payment);
        Payment byExternalReference = paymentUseCase.findByExternalReference("852369");
        assertEquals("852369", byExternalReference.getExternalReference());
    }

    @Test
    void testFindByExternalReferenceSuccess() {
        Payment byExternalReference = paymentUseCase.findByExternalReference("1230002");
        assertEquals("1230002", byExternalReference.getExternalReference());
    }

    @Test
    void testFindByExternalReferenceNotFound() {
        var ex = assertThrows( NotFoundException.class, ()-> paymentUseCase.findByExternalReference("1230004")) ;
        assertEquals("Payment not found for externalReference: 1230004", ex.getMessage());
    }

    @Test
    void testGeneratePaymentAndResponseQRCode() {
        OrderResponseML responseML = mock.getOrderResponseML();
        when(httpRequestML.sendOrderToMl(anyString(), any(OrdersML.class))).thenReturn(responseML);
        PaymentQRCode paymentQRCode = paymentUseCase.generatePayment("1230003", "http-test-url-for-webhook", 2L);
        assertNotNull(paymentQRCode.getQrCode());
    }

    @Test
    void testGeneratePaymentAndResponseInvalidQRCode() {
        OrderResponseML responseML = new OrderResponseML("2121", "");
        when(httpRequestML.sendOrderToMl(anyString(), any(OrdersML.class))).thenReturn(responseML);
        var ex = assertThrows(BusinessException.class, () -> paymentUseCase
                .generatePayment("1230003", "http-test-url-for-webhook", 2L));
        assertEquals("Fail to generate QR Code", ex.getMessage());
    }

    @Test
    void testGeneratePaymentFailToFindPayment() {
        var ex = assertThrows(NotFoundException.class, () ->  paymentUseCase
                .generatePayment("1230004", "http-test-url-for-webhook", 2L));

        assertEquals("Payment not found for send with order: 1230004", ex.getMessage());
    }

    @Test
    void testWebhookWithSucess() {
        String resource = "http//test-webhook";
        String orderId = "1230001";
        PaymentResponseML paid = PaymentResponseML.builder().externalReference(orderId).orderStatus("paid").build();
        when(httpRequestML.findPayment(anyString(), anyString())).thenReturn(paid);

        paymentUseCase.webhook(resource);

        Payment byExternalReference = paymentUseCase.findByExternalReference(orderId);
        assertFalse(byExternalReference.getSent());
        assertTrue(byExternalReference.isPaid());
        assertEquals("1230001", byExternalReference.getExternalReference());

    }

    @Test
    void testWebhookPaymentNotFound() {
        String resource = "http//test-webhook";
        String orderId = "1230005";
        PaymentResponseML paid = PaymentResponseML.builder().externalReference(orderId).orderStatus("paid").build();
        when(httpRequestML.findPayment(anyString(), anyString())).thenReturn(paid);
        try {
            paymentUseCase.webhook(resource);
        }catch (NotFoundException ex){
            assertEquals("Payment not found for externalReference: 1230005", ex.getMessage());
        }
    }

    @Test
    void testWebhookWithParamNull() {
        var ex = assertThrows(BusinessException.class, () -> paymentUseCase.webhook(null)) ;
        assertEquals("Resource can't be null!", ex.getMessage());
    }

    private void clearDb() {
        paymentRepository.deleteAll();
    }

    private void insertPaymentDB() {
        Payment paymentMock1 = mock.getPaymentMock("1230001");
        Payment paymentMock2 = mock.getPaymentMock("1230002");
        Payment paymentMock3 = mock.getPaymentMock("1230003");
        paymentUseCase.save(paymentMock1);
        paymentUseCase.save(paymentMock2);
        paymentUseCase.save(paymentMock3);
    }
}
