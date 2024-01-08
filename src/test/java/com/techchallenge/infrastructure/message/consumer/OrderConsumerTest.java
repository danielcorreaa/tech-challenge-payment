package com.techchallenge.infrastructure.message.consumer;


import com.mongodb.MongoException;
import com.techchallenge.application.gateway.PaymentExternalGateway;
import com.techchallenge.application.gateway.PaymentGateway;
import com.techchallenge.application.usecase.PaymentUseCase;
import com.techchallenge.application.usecase.interactor.PaymentUseCaseInteractor;
import com.techchallenge.core.response.JsonUtils;
import com.techchallenge.core.response.ObjectMapperConfig;
import com.techchallenge.core.utils.FileUtils;
import com.techchallenge.domain.entity.Payment;
import com.techchallenge.infrastructure.consumer.OrderConsumer;
import com.techchallenge.infrastructure.gateways.PaymentRepositoryGateway;
import com.techchallenge.infrastructure.message.consumer.dto.OrderDto;
import com.techchallenge.infrastructure.message.consumer.mapper.OrderMessageMapper;
import com.techchallenge.infrastructure.persistence.documents.PaymentDocument;
import com.techchallenge.infrastructure.persistence.mapper.PaymentDocumentMapper;
import com.techchallenge.infrastructure.persistence.repository.PaymentRepository;
import com.techchallenge.util.ObjectMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;


import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class OrderConsumerTest {

    OrderConsumer orderConsumer;

    OrderDto orderDto;

    JsonUtils jsonUtils;

    PaymentUseCase paymentUseCase;

    OrderMessageMapper mapper;

    @Mock
    PaymentExternalGateway paymentExternalGateway;
    PaymentGateway paymentGateway;

    @Spy
    PaymentRepository paymentRepository;

    PaymentDocumentMapper paymentDocumentMapper;

    String title = "Lanchonete Tech Challenge";

    String description = "Lanchonete Tech Challenge, Lanches, Bebidas, Sobremesas";


    @BeforeEach
    void init(){
        jsonUtils = new JsonUtils(new ObjectMapperConfig().objectMapper());
        orderDto = jsonUtils.parse(new FileUtils().getFile("/data/order.json"), OrderDto.class).orElse(null);

        paymentDocumentMapper = new PaymentDocumentMapper();
        mapper = new OrderMessageMapper();
        paymentGateway = new PaymentRepositoryGateway(paymentRepository, paymentDocumentMapper);
        paymentUseCase = new PaymentUseCaseInteractor(paymentExternalGateway, paymentGateway);
        orderConsumer = new OrderConsumer(paymentUseCase, mapper);
        ReflectionTestUtils.setField(orderConsumer, "title", "Lanchonete Tech Challenge");
        ReflectionTestUtils.setField(orderConsumer, "description", "Lanchonete Tech Challenge, Lanches, Bebidas, Sobremesas");
    }

    @Test
    void testListenOrdersAndCreatePayment(){
        Payment payment = mapper.toPayment(title, description, orderDto);
        PaymentDocument paymentDocument = paymentDocumentMapper.toPaymentDocument(payment);
        when(paymentRepository.save(any(PaymentDocument.class))).thenReturn(paymentDocument);
        Acknowledgment ack = spy(Acknowledgment.class);
        orderConsumer.listenOrders(orderDto, ack);
        verify(paymentRepository, times(1)).save(any(PaymentDocument.class));
        verify(ack, times(1)).acknowledge();
    }

    @Test
    void testListenOrders_FailedToSave(){
        Payment payment = mapper.toPayment(title, description, orderDto);
        PaymentDocument paymentDocument = paymentDocumentMapper.toPaymentDocument(payment);
        when(paymentRepository.save(any(PaymentDocument.class))).thenThrow(MongoException.class);
        Acknowledgment ack = spy(Acknowledgment.class);
        orderConsumer.listenOrders(orderDto, ack);
        verify(paymentRepository, Mockito.times(1)).save(any(PaymentDocument.class));
        verify(ack, never()).acknowledge();
    }

}