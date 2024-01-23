package com.techchallenge.infrastructure.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.techchallenge.application.gateway.PaymentExternalGateway;
import com.techchallenge.application.gateway.PaymentGateway;
import com.techchallenge.application.usecase.PaymentUseCase;
import com.techchallenge.application.usecase.interactor.PaymentUseCaseInteractor;
import com.techchallenge.core.exceptions.handler.ExceptionHandlerConfig;
import com.techchallenge.core.response.JsonUtils;
import com.techchallenge.core.response.ObjectMapperConfig;
import com.techchallenge.core.response.Result;
import com.techchallenge.domain.entity.Payment;
import com.techchallenge.domain.valueobject.Item;
import com.techchallenge.infrastructure.api.mapper.PaymentMapper;
import com.techchallenge.infrastructure.api.request.PayRequest;
import com.techchallenge.infrastructure.api.request.PaymentResponse;
import com.techchallenge.infrastructure.api.request.PaymentWebhookRequest;
import com.techchallenge.infrastructure.external.HttpRequestML;
import com.techchallenge.infrastructure.external.dtos.OrderResponseML;
import com.techchallenge.infrastructure.external.dtos.OrdersML;
import com.techchallenge.infrastructure.external.mapper.OrderMLMapper;
import com.techchallenge.infrastructure.gateways.PaymentIntegrationMLGateway;
import com.techchallenge.infrastructure.gateways.PaymentRepositoryGateway;
import com.techchallenge.infrastructure.persistence.documents.PaymentDocument;
import com.techchallenge.infrastructure.persistence.mapper.PaymentDocumentMapper;
import com.techchallenge.infrastructure.persistence.repository.PaymentRepository;
import com.techchallenge.util.ObjectMock;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
class PaymentApiTest {

    MockMvc mockMvc;
    PaymentApi paymentApi;
    PaymentUseCase paymentUseCase;
    PaymentMapper paymentMapper;

    @Mock
    private PaymentExternalGateway paymentExternalGateway;
    private PaymentGateway paymentGateway;

    @Mock
    private PaymentRepository paymentRepository;
    private PaymentDocumentMapper paymentDocumentMapper;

    @Mock
    private HttpRequestML httpRequestML;
    private OrderMLMapper orderMLMapper;

    JsonUtils jsonUtils;

    ObjectMock mock;


    @BeforeEach
    void init(){
        mock = new ObjectMock();
        jsonUtils = new JsonUtils(new ObjectMapperConfig().objectMapper());
        paymentDocumentMapper = new PaymentDocumentMapper();
        paymentGateway = new PaymentRepositoryGateway(paymentRepository, paymentDocumentMapper);

        orderMLMapper = new OrderMLMapper();
        paymentExternalGateway = new PaymentIntegrationMLGateway(httpRequestML, orderMLMapper);
        ReflectionTestUtils.setField(paymentExternalGateway, "token", "test");
        paymentUseCase = new PaymentUseCaseInteractor(paymentExternalGateway, paymentGateway);

        paymentMapper = new PaymentMapper();
        paymentApi = new PaymentApi(paymentUseCase, paymentMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentApi).setControllerAdvice(new ExceptionHandlerConfig()).build();
    }

    @Nested
    class TestCheckout {
        @Test
        void testCheckoutWithSucess() throws Exception {
            String orderId = "022001";
            String jsonRequest = jsonUtils.toJson(new PayRequest(orderId)).orElse("");

            Payment payment = mock.getPaymentMock(orderId);

            PaymentDocument paymentDocument = paymentDocumentMapper.toPaymentDocument(payment);

            when(paymentRepository.findById(orderId)).thenReturn(Optional.of(paymentDocument));

            when(paymentRepository.save(any(PaymentDocument.class))).thenReturn(paymentDocument);

            when(httpRequestML.sendOrderToMl(anyString(), any(OrdersML.class))).thenReturn(mock.getOrderResponseML());

            MvcResult mvcResult = mockMvc.perform(post("/api/v1/payment/pay")
                            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
                    .andExpect(status().isOk()).andReturn();

            assertNotNull(mvcResult.getResponse());
            assertEquals("image/png", mvcResult.getResponse().getContentType());
            verify(paymentRepository, times(1)).save(any(PaymentDocument.class));
        }

        @Test
        void testCheckout_orderIdNotFound() throws Exception {
            String orderId = "022001";
            String jsonRequest = jsonUtils.toJson(new PayRequest(orderId)).orElse("");


            when(httpRequestML.sendOrderToMl(anyString(), any(OrdersML.class))).thenReturn(mock.getOrderResponseML());

            MvcResult mvcResult = mockMvc.perform(post("/api/v1/payment/pay")
                            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
                    .andExpect(status().isNotFound()).andReturn();

            Optional<Result> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result>() {
                    });

            assertNotNull(mvcResult.getResponse());
            assertEquals(404, response.get().getCode());
            assertEquals("Payment not found for send with order: 022001", response.get().getErrors().get(0));
        }

        @Test
        void testCheckoutWithSucess_notificationUrlIsNotEmpty() throws Exception {
            String orderId = "022001";
            String jsonRequest = jsonUtils.toJson(new PayRequest(orderId)).orElse("");

            Payment payment = mock.getPaymentMock(orderId);
            PaymentDocument paymentDocument = paymentDocumentMapper.toPaymentDocument(payment);

            when(paymentRepository.findById(orderId)).thenReturn(Optional.of(paymentDocument));

            when(httpRequestML.sendOrderToMl(anyString(), any(OrdersML.class))).thenReturn(mock.getOrderResponseML());

            when(paymentRepository.save(any(PaymentDocument.class))).thenReturn(paymentDocument);

            MvcResult mvcResult = mockMvc.perform(post("/api/v1/payment/pay")
                            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
                    .andExpect(status().isOk()).andReturn();

            assertNotNull(mvcResult.getResponse());
            assertEquals("image/png", mvcResult.getResponse().getContentType());

            verify(paymentRepository, times(1)).save(any(PaymentDocument.class));
        }


        @Test
        void testCheckout_errorSendRequestToML() throws Exception {
            String orderId = "022001";
            String jsonRequest = jsonUtils.toJson(new PayRequest(orderId)).orElse("");

            Payment payment = mock.getPaymentMock(orderId);

            PaymentDocument paymentDocument = paymentDocumentMapper.toPaymentDocument(payment);

            when(paymentRepository.findById(orderId)).thenReturn(Optional.of(paymentDocument));

            when(paymentRepository.save(any(PaymentDocument.class))).thenReturn(paymentDocument);

            when(httpRequestML.sendOrderToMl(anyString(), any(OrdersML.class))).thenThrow(FeignException.FeignClientException.class);

            MvcResult mvcResult = mockMvc.perform(post("/api/v1/payment/pay")
                            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
                    .andExpect(status().isBadRequest()).andReturn();

            Optional<Result> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result>() {
                    });

            assertNotNull(mvcResult.getResponse());
            assertEquals(400, response.get().getCode());
            assertEquals("Error to sendPayment for Mercado Pago", response.get().getErrors().get(0));
        }

        @Test
        void testCheckout_sendRequestToMLWithSucess_responseNull() throws Exception {
            String orderId = "022001";
            String jsonRequest = jsonUtils.toJson(new PayRequest(orderId)).orElse("");

            Payment payment = mock.getPaymentMock(orderId);

            PaymentDocument paymentDocument = paymentDocumentMapper.toPaymentDocument(payment);

            when(paymentRepository.findById(orderId)).thenReturn(Optional.of(paymentDocument));

            when(paymentRepository.save(any(PaymentDocument.class))).thenReturn(paymentDocument);

            when(httpRequestML.sendOrderToMl(anyString(), any(OrdersML.class))).thenReturn(null);

            MvcResult mvcResult = mockMvc.perform(post("/api/v1/payment/pay")
                            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
                    .andExpect(status().isBadRequest()).andReturn();

            Optional<Result> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result>() {
                    });

            assertNotNull(mvcResult.getResponse());
            assertEquals(400, response.get().getCode());
            assertEquals("Fail to get QR code from mercado livre", response.get().getErrors().get(0));
        }

        @Test
        void testCheckout_sendRequestToMLWithSucess_invalidQrCode() throws Exception {
            String orderId = "022001";
            String jsonRequest = jsonUtils.toJson(new PayRequest(orderId)).orElse("");

            Payment payment = mock.getPaymentMock(orderId);

            PaymentDocument paymentDocument = paymentDocumentMapper.toPaymentDocument(payment);

            when(paymentRepository.findById(orderId)).thenReturn(Optional.of(paymentDocument));

            when(paymentRepository.save(any(PaymentDocument.class))).thenReturn(paymentDocument);

            when(httpRequestML.sendOrderToMl(anyString(), any(OrdersML.class))).thenReturn(mock.getOrderResponseMLInvalid());

            MvcResult mvcResult = mockMvc.perform(post("/api/v1/payment/pay")
                            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
                    .andExpect(status().isBadRequest()).andReturn();

            Optional<Result> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result>() {
                    });

            assertNotNull(mvcResult.getResponse());
            assertEquals(400, response.get().getCode());
            assertEquals("Fail to generate QR Code", response.get().getErrors().get(0));
        }
    }


    @Nested
    class TestWebhook {
        @Test
        void testWebhookWithSuccess() throws Exception {
            String orderId = "022001";
            PaymentWebhookRequest paymentWebhookRequest = new PaymentWebhookRequest("http//mercadolivre/022001", "", "");
            String jsonRequest = jsonUtils.toJson(paymentWebhookRequest).orElse("");
            Payment payment = mock.getPaymentMock(orderId);
            PaymentDocument paymentDocument = paymentDocumentMapper.toPaymentDocument(payment);

            when(httpRequestML.findPayment(anyString(), anyString())).thenReturn(mock.getPaymentResponseML(orderId));
            when(paymentRepository.findById(orderId)).thenReturn(Optional.of(paymentDocument));
            when(paymentRepository.save(any(PaymentDocument.class))).thenReturn(paymentDocument);
            MvcResult mvcResult = mockMvc.perform(post("/api/v1/payment/webhook")
                            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
                    .andExpect(status().isOk()).andReturn();

            Optional<Result> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result>() {
                    });

            assertNotNull(mvcResult.getResponse());
            assertEquals(200, response.get().getCode());
            assertEquals("Webhook process with success!", response.get().getBody());

            verify(paymentRepository, times(1)).findById(orderId);
            verify(paymentRepository, times(1)).save(any(PaymentDocument.class));
            verify(httpRequestML, times(1)).findPayment(anyString(), anyString());
        }

        @Test
        void testWebhook_resouceIsEmpty() throws Exception {
            PaymentWebhookRequest paymentWebhookRequest = new PaymentWebhookRequest("", "", "");
            String jsonRequest = jsonUtils.toJson(paymentWebhookRequest).orElse("");

            MvcResult mvcResult = mockMvc.perform(post("/api/v1/payment/webhook")
                            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
                    .andExpect(status().isBadRequest()).andReturn();

            Optional<Result> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result>() {
                    });

            assertNotNull(mvcResult.getResponse());
            assertEquals(400, response.get().getCode());
            assertEquals("Resource can't be null!", response.get().getErrors().get(0));

            verify(paymentRepository, never()).findById(anyString());
            verify(paymentRepository, never()).save(any(PaymentDocument.class));
            verify(httpRequestML, never()).findPayment(anyString(), anyString());
        }

        @Test
        void testWebhook_withRequestExternalException() throws Exception {

            PaymentWebhookRequest paymentWebhookRequest = new PaymentWebhookRequest("http//mercadolivre/022001", "", "");
            String jsonRequest = jsonUtils.toJson(paymentWebhookRequest).orElse("");

            when(httpRequestML.findPayment(anyString(), anyString())).thenThrow(FeignException.class);

            MvcResult mvcResult = mockMvc.perform(post("/api/v1/payment/webhook")
                            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
                    .andExpect(status().isBadRequest()).andReturn();

            Optional<Result> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result>() {
                    });

            assertNotNull(mvcResult.getResponse());
            assertEquals(400, response.get().getCode());
            assertEquals("Error to check payment in Mercado Pago!", response.get().getErrors().get(0));

            verify(paymentRepository, never()).findById(anyString());
            verify(paymentRepository, never()).save(any(PaymentDocument.class));
            verify(httpRequestML, times(1)).findPayment(anyString(), anyString());
        }

        @Test
        void testWebhook_idNotFound() throws Exception {
            String orderId = "022001";
            PaymentWebhookRequest paymentWebhookRequest = new PaymentWebhookRequest("http//mercadolivre/022001", "", "");
            String jsonRequest = jsonUtils.toJson(paymentWebhookRequest).orElse("");
            Payment payment = mock.getPaymentMock(orderId);
            PaymentDocument paymentDocument = paymentDocumentMapper.toPaymentDocument(payment);

            when(httpRequestML.findPayment(anyString(), anyString())).thenReturn(mock.getPaymentResponseML(orderId));
            when(paymentRepository.findById(orderId)).thenReturn(Optional.empty());

            MvcResult mvcResult = mockMvc.perform(post("/api/v1/payment/webhook")
                            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
                    .andExpect(status().isNotFound()).andReturn();

            Optional<Result> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result>() {
                    });

            assertNotNull(mvcResult.getResponse());
            assertEquals(404, response.get().getCode());
            assertEquals("Payment not found for externalReference: 022001", response.get().getErrors().get(0));

            verify(paymentRepository, times(1)).findById(orderId);
            verify(paymentRepository, never()).save(any(PaymentDocument.class));
            verify(httpRequestML, times(1)).findPayment(anyString(), anyString());
        }
    }

    @Nested
    class TestFindPayment {
        @Test
        void testFindbyExternalReferenceWithSucsess() throws Exception {
            String orderId = "022001";
            Payment payment = mock.getPaymentMock(orderId);

            PaymentDocument paymentDocument = paymentDocumentMapper.toPaymentDocument(payment);

            when(paymentRepository.findById(orderId)).thenReturn(Optional.of(paymentDocument));

            MvcResult mvcResult = mockMvc.perform(get("/api/v1/payment/find/" + orderId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andReturn();

            Optional<Result<PaymentResponse>> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result<PaymentResponse>>() {
                    });


            PaymentResponse body = response.get().getBody();
            assertEquals(200, response.get().getCode());
            assertEquals("022001", body.externalReference());
            assertEquals(2, body.items().size());
            assertFalse(body.sent());
            assertNull(body.orderStatus());

            assertEquals("2", body.items().get(0).skuNumber());
            assertEquals("3", body.items().get(1).skuNumber());


            verify(paymentRepository, times(1)).findById(orderId);

        }

        @Test
        void testFindbyExternalReference_notFound() throws Exception {
            String orderId = "022001";
            Payment payment = mock.getPaymentMock(orderId);

            when(paymentRepository.findById(orderId)).thenReturn(Optional.empty());

            MvcResult mvcResult = mockMvc.perform(get("/api/v1/payment/find/" + orderId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound()).andReturn();

            Optional<Result> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result>() {
                    });

            assertEquals(404, response.get().getCode());
            assertEquals("Payment not found for externalReference: 022001", response.get().getErrors().get(0));

            verify(paymentRepository, times(1)).findById(orderId);

        }
    }


}