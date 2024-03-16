package com.techchallenge.infrastructure.message.produce;

import com.techchallenge.application.usecase.MessageUseCase;
import com.techchallenge.application.usecase.PaymentUseCase;
import com.techchallenge.domain.entity.MessagePayment;
import com.techchallenge.domain.entity.Payment;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Configuration
@EnableScheduling
public class PaymentExpiredProduce {

    private MessageUseCase messageUseCase;
    private PaymentUseCase paymentUseCase;

    private static final long SECOND = 1000;
    private static final long MINUTE = SECOND * 60;

    public PaymentExpiredProduce( @Qualifier(value = "usecase-message-error") MessageUseCase messageUseCase, PaymentUseCase paymentUseCase) {
        this.messageUseCase = messageUseCase;
        this.paymentUseCase = paymentUseCase;
    }

    @Scheduled(fixedDelay = MINUTE)
    public void process(){
        List<Payment> notSendAndIsPaid = paymentUseCase.findPaymentExpired(LocalDateTime.now());
        if(notSendAndIsPaid.isEmpty()) {
            log.info("No message to send");
        }
        notSendAndIsPaid.forEach( send -> {
            log.info("Message to send: " + send);
            messageUseCase.send(new MessagePayment(send.getExternalReference(), send.getOrderStatus(),
                    send.getCpfCustomer().orElse("")));
           send.toSendExpiration();
           paymentUseCase.save(send);
        });
    }
}
