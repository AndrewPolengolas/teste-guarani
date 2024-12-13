package com.example.guarani.sistemas.demo.domain.repository;

import com.example.guarani.sistemas.demo.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
