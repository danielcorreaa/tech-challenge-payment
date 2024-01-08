package com.techchallenge.infrastructure.message.produce;

import com.techchallenge.application.gateway.MessageGateway;
import com.techchallenge.application.gateway.PaymentExternalGateway;
import com.techchallenge.application.gateway.PaymentGateway;
import com.techchallenge.application.usecase.MessageUseCase;
import com.techchallenge.application.usecase.PaymentUseCase;
import com.techchallenge.application.usecase.interactor.MessageUseCaseInteractor;
import com.techchallenge.application.usecase.interactor.PaymentUseCaseInteractor;
import com.techchallenge.domain.entity.MessagePayment;
import com.techchallenge.domain.entity.Payment;
import com.techchallenge.infrastructure.gateways.MessageGatewayInteractor;
import com.techchallenge.infrastructure.gateways.PaymentRepositoryGateway;
import com.techchallenge.infrastructure.persistence.documents.PaymentDocument;
import com.techchallenge.infrastructure.persistence.mapper.PaymentDocumentMapper;
import com.techchallenge.infrastructure.persistence.repository.PaymentRepository;
import com.techchallenge.util.ObjectMock;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.MockProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
class PaymentProduceTest {

    PaymentProduce paymentProduce;

    MessageUseCase messageUseCase;

    PaymentUseCase paymentUseCase;

    PaymentGateway paymentGateway;
    MessageGateway messageGateway;


    @Mock
    KafkaTemplate<String, MessagePayment> kafkaTemplate;;

    ProducerFactory producerFactory;
    @Spy
    PaymentRepository paymentRepository;
    PaymentDocumentMapper paymentDocumentMapper;

    @Mock
    PaymentExternalGateway paymentExternalGateway;

    ObjectMock mock;

    @BeforeEach
    void init(){


        mock = new ObjectMock();
        paymentDocumentMapper = new PaymentDocumentMapper();
        paymentGateway = new PaymentRepositoryGateway(paymentRepository, paymentDocumentMapper);

        messageGateway = new MessageGatewayInteractor(kafkaTemplate);
        ReflectionTestUtils.setField(messageGateway, "topic", "test");

        messageUseCase = new MessageUseCaseInteractor(paymentGateway, messageGateway);

        paymentUseCase = new PaymentUseCaseInteractor(paymentExternalGateway, paymentGateway);
        paymentProduce = new PaymentProduce(messageUseCase, paymentUseCase);
    }

    @Test
    void testProcessMessageWithSuccess(){
        List<Payment> payments =  mock.getPaymentsToPaid();
        List<PaymentDocument> documents = payments.stream().map(p -> paymentDocumentMapper.toPaymentDocument(p)).toList();

        when(paymentRepository.findNotSendAndIsPaid(any(Sort.class))).thenReturn(documents);
        for (PaymentDocument doc : documents) {
            when(paymentRepository.save(any(PaymentDocument.class))).thenReturn(doc);
            CompletableFuture<SendResult<String, MessagePayment>> future = new CompletableFuture<>();
            when(kafkaTemplate.send(any(String.class), any(MessagePayment.class))).thenReturn(future);
        }

        paymentProduce.process();

        verify(paymentRepository, times(1)).findNotSendAndIsPaid(any(Sort.class));
        verify(paymentRepository, times(5)).save(any());
        verify(kafkaTemplate , times(5)).send(any(String.class), any(MessagePayment.class));

    }

}