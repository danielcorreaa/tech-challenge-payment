package com.techchallenge.application.usecase;

import com.techchallenge.application.gateway.MessageGateway;
import com.techchallenge.application.gateway.PaymentExternalGateway;
import com.techchallenge.application.gateway.PaymentGateway;
import com.techchallenge.application.usecase.interactor.PaymentUseCaseInteractor;
import com.techchallenge.core.exceptions.NotFoundException;
import com.techchallenge.domain.entity.Payment;
import com.techchallenge.domain.entity.PaymentQRCode;
import com.techchallenge.util.ObjectMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class PaymentUseCaseTest {

    PaymentUseCase paymentUseCase;
    @Spy
    PaymentExternalGateway paymentExternalGateway;
    @Spy
    PaymentGateway paymentGateway;
    @Spy
    MessageGateway messageGateway;

    ObjectMock mock;

    @BeforeEach
    void init(){
        paymentUseCase = new PaymentUseCaseInteractor(paymentExternalGateway, paymentGateway);
        mock = new ObjectMock();
    }

    @Test
    void testCreatePayment() {
        Payment payment = mock.getPaymentMock("2");
        paymentUseCase.save(payment);
        verify(paymentGateway, times(1)).insert(payment);
    }

    @Test
    void testFindByExternalReferenceSuccess() {
        Payment payment = mock.getPaymentMock("5678");
        when(paymentGateway.findById("5678")).thenReturn(Optional.of(payment));
        Payment pay = paymentUseCase.findByExternalReference("5678");
        assertTrue(Optional.of(pay).isPresent());
        verify(paymentGateway, times(1)).findById(any());
    }

    @Test
    void testFindByExternalReferenceNotFound() {
        when(paymentGateway.findById("5678")).thenReturn(Optional.empty());
        var ex = assertThrows( NotFoundException.class, ()-> paymentUseCase.findByExternalReference("5678")) ;
        assertEquals("Payment not found for externalReference: 5678", ex.getMessage());
        verify(paymentGateway, times(1)).findById("5678");
    }

    @Test
    void testGeneratePaymentAndResponseQRCode() {
        Payment payment = mock.getPaymentMock("5678");

        when(paymentExternalGateway.sendPayment(payment)).thenReturn(new PaymentQRCode(mock(InputStream.class)));
        when(paymentGateway.findById("5678")).thenReturn(Optional.of(payment));

        PaymentQRCode paymentQRCode = paymentUseCase.generatePayment("5678", "http-test-url-for-webhook");

        verify(paymentExternalGateway, times(1)).sendPayment(payment);
        verify(paymentGateway, times(1)).findById("5678");

        assertNotNull(paymentQRCode);
        assertEquals("http-test-url-for-webhook", payment.getNotificationUrl());

    }

    @Test
    void testGeneratePaymentFailToFindPayment() {
        Payment payment = mock.getPaymentMock("85");

        when(paymentExternalGateway.sendPayment(payment)).thenReturn(new PaymentQRCode(mock(InputStream.class)));
                when(paymentGateway.findById("5678")).thenReturn(Optional.empty());

        var ex =assertThrows(NotFoundException.class, () ->  paymentUseCase
                .generatePayment("5678", "http-test-url-for-webhook"));

        verify(paymentExternalGateway, never()).sendPayment(payment);
        verify(paymentGateway, times(1)).findById("5678");

        assertEquals("Payment not found for send with order: 5678", ex.getMessage());

    }

    @Test
    void webhook() {

        String test = String.format("%s%s:%s@%s:%s/%s", "mongodb://", "root", "exemple", "localhost", "20027", "payment");
        System.out.println(test);

    }
}