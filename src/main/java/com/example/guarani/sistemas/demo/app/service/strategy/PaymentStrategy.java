package com.example.guarani.sistemas.demo.app.service.strategy;

import com.example.guarani.sistemas.demo.domain.model.Order;

public interface PaymentStrategy {
    void sendPayment(Order order);
}
