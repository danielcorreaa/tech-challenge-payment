package com.techchallenge.infrastructure.api.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Builder
public record PaymentResponse(
        String externalReference,
        String title,
        String description,
        String notificationUrl,
        BigDecimal totalAmount,
        String orderStatus,
        List<ItemResponse> items,

        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime createTime,
        Boolean sent) {


}
