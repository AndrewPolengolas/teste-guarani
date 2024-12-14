package com.example.guarani.sistemas.demo.infra.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

@Configuration
public class OrderRabbitConfig {

    public static final String QUEUE_BOLETO = "queue.boleto";
    public static final String QUEUE_PIX = "queue.pix";
    public static final String QUEUE_CREDITO = "queue.credito";

    @Bean
    public Queue boletoQueue() {
        return new Queue(QUEUE_BOLETO, true);
    }

    @Bean
    public Queue pixQueue() {
        return new Queue(QUEUE_PIX, true);
    }

    @Bean
    public Queue creditoQueue() {
        return new Queue(QUEUE_CREDITO, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("paymentExchange");
    }

    @Bean
    public Binding boletoBinding(Queue boletoQueue, TopicExchange exchange) {
        return BindingBuilder.bind(boletoQueue).to(exchange).with("payment.boleto");
    }

    @Bean
    public Binding pixBinding(Queue pixQueue, TopicExchange exchange) {
        return BindingBuilder.bind(pixQueue).to(exchange).with("payment.pix");
    }

    @Bean
    public Binding creditoBinding(Queue creditoQueue, TopicExchange exchange) {
        return BindingBuilder.bind(creditoQueue).to(exchange).with("payment.credito");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter(new ObjectMapper());
    }
}
