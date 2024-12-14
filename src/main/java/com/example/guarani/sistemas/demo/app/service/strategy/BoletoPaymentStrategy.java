package com.example.guarani.sistemas.demo.app.service.strategy;

import com.example.guarani.sistemas.demo.domain.model.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;


@Component("BOLETO")
public class BoletoPaymentStrategy implements PaymentStrategy {
    private final RabbitTemplate rabbitTemplate;

    public BoletoPaymentStrategy(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendPayment(Order order) {
        rabbitTemplate.convertAndSend("paymentExchange", "payment.boleto", order);
    }
}
