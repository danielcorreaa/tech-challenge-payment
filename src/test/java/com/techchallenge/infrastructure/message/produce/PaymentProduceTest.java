package com.techchallenge.infrastructure.message.produce;

import com.techchallenge.application.gateway.PaymentExternalGateway;
import com.techchallenge.application.gateway.PaymentGateway;
import com.techchallenge.application.usecase.MessageUseCase;
import com.techchallenge.application.usecase.PaymentUseCase;
import com.techchallenge.application.usecase.interactor.MessageUseCaseInteractor;
import com.techchallenge.application.usecase.interactor.PaymentUseCaseInteractor;
import com.techchallenge.core.kafka.produce.TopicProducer;
import com.techchallenge.domain.entity.MessagePayment;
import com.techchallenge.domain.entity.Payment;
import com.techchallenge.infrastructure.gateways.PaymentRepositoryGateway;
import com.techchallenge.infrastructure.persistence.documents.PaymentDocument;
import com.techchallenge.infrastructure.persistence.mapper.PaymentDocumentMapper;
import com.techchallenge.infrastructure.persistence.repository.PaymentRepository;
import com.techchallenge.util.ObjectMock;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
class PaymentProduceTest {

    PaymentProduce paymentProduce;

    MessageUseCase messageUseCase;

    PaymentUseCase paymentUseCase;

    PaymentGateway paymentGateway;

    ProducerFactory producerFactory;
    @Spy
    PaymentRepository paymentRepository;
    PaymentDocumentMapper paymentDocumentMapper;

    @Mock
    PaymentExternalGateway paymentExternalGateway;

    @Mock
    TopicProducer<MessagePayment> topicProducer;

    ObjectMock mock;

    @BeforeEach
    void init(){
        mock = new ObjectMock();
        paymentDocumentMapper = new PaymentDocumentMapper();

        paymentGateway = new PaymentRepositoryGateway(paymentRepository, paymentDocumentMapper);
        messageUseCase = new MessageUseCaseInteractor(topicProducer);

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

            ProducerRecord<String, MessagePayment> producerRecord =
                    new ProducerRecord<>("test", new MessagePayment("5214","paid"));
            RecordMetadata recordMetadata = mock(RecordMetadata.class);

            SendResult<String, MessagePayment> sendResult = new SendResult<>(producerRecord, recordMetadata);
            when(topicProducer.produce(any(String.class), any(MessagePayment.class))).thenReturn(sendResult);
        }

        paymentProduce.process();

        verify(paymentRepository, times(1)).findNotSendAndIsPaid(any(Sort.class));
        verify(paymentRepository, times(5)).save(any());
        verify(topicProducer , times(5)).produce(any(String.class), any(MessagePayment.class));
    }

    @Test
    void testProcessMessageNoMessageToProduce(){
        when(paymentRepository.findNotSendAndIsPaid(any(Sort.class))).thenReturn(List.of());

        paymentProduce.process();

        verify(paymentRepository, times(1)).findNotSendAndIsPaid(any(Sort.class));
        verify(paymentRepository, never()).save(any());
        verify(topicProducer ,never()).produce(any(String.class), any(MessagePayment.class));
    }

}