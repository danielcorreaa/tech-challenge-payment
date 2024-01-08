package com.techchallenge.domain.entity;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentQRCodeTest {

    @Test
    void testCreatePaymentQRCode(){
        InputStream io = Mockito.mock(InputStream.class);
        assertNotNull(new PaymentQRCode(io));
    }

    @Test
    void testCreatePaymentQRCodeNull() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> new PaymentQRCode(null));
        assertEquals("Qr code can't be null", ex.getMessage());
    }

}