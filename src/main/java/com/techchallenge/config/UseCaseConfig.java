package com.techchallenge.config;


import com.techchallenge.application.gateway.PaymentExternalGateway;
import com.techchallenge.application.gateway.PaymentGateway;
import com.techchallenge.application.usecase.MessageUseCase;
import com.techchallenge.application.usecase.PaymentUseCase;
import com.techchallenge.application.usecase.interactor.MessageUseCaseInteractor;
import com.techchallenge.application.usecase.interactor.PaymentUseCaseInteractor;
import com.techchallenge.core.kafka.produce.TopicProducer;
import com.techchallenge.domain.entity.MessagePayment;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {
	
	@Bean
	public PaymentUseCase paymentUseCase(PaymentExternalGateway paymentExternalGateway, PaymentGateway paymentGateway) {
		return new PaymentUseCaseInteractor(paymentExternalGateway, paymentGateway);
	}

	@Bean(name = "usecase-message-success")
	public MessageUseCase messageUseCase(@Qualifier(value = "payment-success") TopicProducer<MessagePayment> topicProducer){
		return new MessageUseCaseInteractor(topicProducer);
	}

	@Bean(name = "usecase-message-error")
	public MessageUseCase messageUseCaseError(@Qualifier(value = "payment-error") TopicProducer<MessagePayment> topicProducer){
		return new MessageUseCaseInteractor(topicProducer);
	}

	
}
