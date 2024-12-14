package com.example.guarani.sistemas.demo.app.service.strategy;

import com.example.guarani.sistemas.demo.domain.model.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.example.guarani.sistemas.demo.infra.config.OrderRabbitConfig.QUEUE_PIX;

@Component("PIX")
public class PixPaymentStrategy implements PaymentStrategy {
    private final RabbitTemplate rabbitTemplate;

    public PixPaymentStrategy(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendPayment(Order order) {
        rabbitTemplate.convertAndSend("paymentExchange", "payment.pix", order);
    }
}
