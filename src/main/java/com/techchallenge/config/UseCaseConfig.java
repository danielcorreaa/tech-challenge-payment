package com.techchallenge.config;


import com.techchallenge.application.gateway.MessageGateway;
import com.techchallenge.application.gateway.PaymentExternalGateway;
import com.techchallenge.application.gateway.PaymentGateway;
import com.techchallenge.application.usecase.PaymentUseCase;
import com.techchallenge.application.usecase.interactor.PaymentUseCaseInteractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {
	
	@Bean
	public PaymentUseCase paymentUseCase(PaymentExternalGateway paymentExternalGateway, PaymentGateway paymentGateway) {
		return new PaymentUseCaseInteractor(paymentExternalGateway, paymentGateway);
	}

	
	
	
}
