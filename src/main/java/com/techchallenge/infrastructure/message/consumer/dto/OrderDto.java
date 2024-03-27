package com.techchallenge.infrastructure.message.consumer.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public record OrderDto(
        @JsonAlias("id")
        String orderId, CustomerDto customer, List<ProductDto> products ) {

        public String cpf(){
             if (null != customer){
                 return  customer.cpf();
             }
             return null;
        }


}
