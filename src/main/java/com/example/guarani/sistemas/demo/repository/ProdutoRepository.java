package com.example.guarani.sistemas.demo.repository;

import com.example.guarani.sistemas.demo.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {}
