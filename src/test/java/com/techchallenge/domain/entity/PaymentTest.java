package com.techchallenge.domain.entity;
;
import com.techchallenge.core.response.JsonUtils;
import com.techchallenge.core.response.ObjectMapperConfig;
import com.techchallenge.core.utils.FileUtils;
import com.techchallenge.domain.valueobject.Item;
import com.techchallenge.infrastructure.api.mapper.PaymentMapper;
import com.techchallenge.infrastructure.api.request.PaymentRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    void testCreatePayment(){
        String file = new FileUtils().getFile("/data/payment.json");
        JsonUtils utils = new JsonUtils(new ObjectMapperConfig().objectMapper());
        Optional<PaymentRequest> paymentRequest = utils.parse(file, PaymentRequest.class);
        PaymentMapper mapper = new PaymentMapper();
        Payment payment =  mapper.toPayment(paymentRequest.get());
        Item item1 = payment.getItems().stream()
                .filter(item -> item.getSkuNumber().equals("1")).findFirst().get();

        assertTrue(Optional.ofNullable(payment).isPresent(), "Must be present!");

        assertEquals("854758", payment.getExternalReference());
        assertEquals("https://eo5swulfz2hoccg.m.pipedream.net", payment.getNotificationUrl());
        assertEquals("Lanchonete Checkout", payment.getTitle());
        assertEquals("Checkout", payment.getDescription());
        assertEquals(new BigDecimal("20.20"), payment.getTotalAmount());
        assertEquals(2, payment.getItems().size());

        assertEquals("LANCHE", item1.getCategory());
        assertEquals("lanche",item1.getDescription());
        assertEquals("1",item1.getSkuNumber());
        assertEquals("X SALADA", item1.getTitle());
        assertEquals("unit", item1.getUnitMeasure());
        assertEquals(1, item1.getQuantity());
        assertEquals(new BigDecimal("10.10"), item1.getUnitPrice());
        assertEquals(new BigDecimal("10.10"), item1.getTotalAmount());
    }

    @Nested
    class TestValidatePayment {
        @Test
        void testValidationExternalReference() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> new Payment("", "", "", "", List.of()));
            assertEquals("External Reference can't be null or empty", ex.getMessage());
        }

        @Test
        void testValidationTitle() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> new Payment("2323", "", "", "", List.of()));
            assertEquals("Title can't be null or empty", ex.getMessage());
        }

        @Test
        void testValidationDescription() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> new Payment("1212", "test", null, "", List.of()));

            assertEquals("Description can't be null or empty", ex.getMessage());
        }

        @Test
        void testValidationItemsEmpty() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> new Payment("1212", "test", "test", "test", List.of()));

            assertEquals("Items can't be null", ex.getMessage());
        }

        @Test
        void testValidationItemsNull() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> new Payment("1212", "test", "test", "test", null));

            assertEquals("Items can't be null", ex.getMessage());
        }
    }

    @Nested
    class TestValidateItems {

        @Test
        void testValidationItemSkuNumber() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> new Payment("1212", "test", "test", "test",
                            List.of(new Item("", "", "", "", BigDecimal.ZERO, 0))));
            assertEquals("Sku Number can't be null or empty", ex.getMessage());
        }

        @Test
        void testValidationItemCategory() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> new Payment("1212", "test", "test", "test",
                            List.of(new Item("344", "", "", "", BigDecimal.ZERO, 0))));
            assertEquals("Category can't be null or empty", ex.getMessage());
        }

        @Test
        void testValidationItemTitle() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> new Payment("1212", "test", "test", "test",
                            List.of(new Item("323", "test", "", "", BigDecimal.ZERO, 0))));
            assertEquals("Title can't be null or empty", ex.getMessage());
        }

        @Test
        void testValidationItemDescription() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> new Payment("1212", "test", "test", "test",
                            List.of(new Item("323", "test", "test", null, BigDecimal.ZERO, 0))));
            assertEquals("Description can't be null or empty", ex.getMessage());
        }

        @Test
        void testValidationItemUnitPrice() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> new Payment("1212", "test", "test", "test",
                            List.of(new Item("323", "test", "test", "test", BigDecimal.ZERO, 0))));
            assertEquals("Unit Price can't be null or 0", ex.getMessage());
        }

        @Test
        void testValidationItemQuantity() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> new Payment("1212", "test", "test", "test",
                            List.of(new Item("323", "test", "test", "test",
                                    new BigDecimal("20"), 0))));
            assertEquals("Quantity can't be null or 0", ex.getMessage());
        }
    }



}