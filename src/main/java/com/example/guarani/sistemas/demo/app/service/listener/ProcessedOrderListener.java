package com.example.guarani.sistemas.demo.app.service.listener;

import com.example.guarani.sistemas.demo.app.dto.order.ProcessedOrderMessage;
import com.example.guarani.sistemas.demo.app.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ProcessedOrderListener {

    private static final Logger logger = LoggerFactory.getLogger(ProcessedOrderListener.class);
    private final OrderService orderService;

    public ProcessedOrderListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = "queue.order.processed")
    public void listenProcessedOrder(ProcessedOrderMessage message) {
        logger.info("Received processed order: {}", message);

        orderService.updatePayment(message);

        logger.info("Order ID {} updated with status: {}", message.getOrderId(), (message.isSuccess() ? "SUCCESS" : "FAILED"));
    }
}
