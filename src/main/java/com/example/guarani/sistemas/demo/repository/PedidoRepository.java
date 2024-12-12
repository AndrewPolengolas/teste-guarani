package com.example.guarani.sistemas.demo.repository;

import com.example.guarani.sistemas.demo.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {}