package com.techchallenge.domain.valueobject;

import com.techchallenge.core.exceptions.BusinessException;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Validation {

    public static String validate(String value, String fieldName) {
        return Optional.ofNullable(value).filter(v -> !v.trim().isEmpty())
                .orElseThrow(() -> new IllegalArgumentException(fieldName + " can't be null or empty"));
    }

    public static BigDecimal validate(BigDecimal value, String fieldName) {
        return Optional.ofNullable(value).filter(v -> !v.equals(BigDecimal.ZERO))
                .orElseThrow(() -> new IllegalArgumentException(fieldName + " can't be null or 0"));
    }

    public static Integer validate(Integer value, String fieldName) {
        return Optional.ofNullable(value).filter(v -> v != 0)
                .orElseThrow(() -> new IllegalArgumentException(fieldName + " can't be null or 0"));
    }

    public static String validateExternalReference(String value) {
        return validate(value, "External Reference");
    }

    public static String validateTitle(String value) {
        return validate(value, "Title");
    }

    public static String validateDescription(String value) {
        return validate(value, "Description");
    }

    public static List<Item> validateItems(List<Item> items) {
        return Optional.ofNullable(items)
                .orElseThrow(() -> new IllegalArgumentException("Items can't be null"))
                .stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), result -> {
                    if (result.isEmpty())
                        throw new IllegalArgumentException("Items can't be null");
                    return result;
                }));
    }

    public static String validateSkuNumber(String value) {
        return validate(value, "Sku Number");
    }

    public static String validateCategory(String value) {
        return validate(value, "Category");
    }

    public static BigDecimal validateUnitPrice(BigDecimal value) {
        return validate(value, "Unit Price");
    }


    public static Integer validateQuantity(Integer value) {
        return validate(value, "Quantity");
    }

    public static InputStream validateQrCode(InputStream qrCode) {
        return Optional.ofNullable(qrCode).orElseThrow(() -> new IllegalArgumentException("Qr code can't be null"));
    }

    public static String validateOrderStatus(String orderStatus) {
        if("paid".equals(orderStatus) ) return validate(orderStatus, "Order Status");
        throw new BusinessException("Invalid Message to sent, status is: " +orderStatus);
    }
}