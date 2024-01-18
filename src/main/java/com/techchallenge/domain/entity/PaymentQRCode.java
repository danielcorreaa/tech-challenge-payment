package com.techchallenge.domain.entity;

import com.techchallenge.domain.valueobject.Validation;

import java.io.InputStream;

public class PaymentQRCode {

    private InputStream qrCode;

    public PaymentQRCode(InputStream qrCode) {
        this.qrCode = Validation.validateQrCode(qrCode);
    }

    public InputStream getQrCode() {
        return qrCode;
    }
}
