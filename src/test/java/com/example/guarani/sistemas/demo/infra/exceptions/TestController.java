package com.example.guarani.sistemas.demo.infra.exceptions;

import com.example.guarani.sistemas.demo.infra.exceptions.custom.OutOfStockException;
import com.example.guarani.sistemas.demo.infra.exceptions.custom.ResourceNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class TestController {

    @GetMapping("/test/resource-not-found")
    public void resourceNotFound() {
        throw new ResourceNotFoundException("Resource not found");
    }

    @GetMapping("/test/validation-error")
    public void validationError() {
        throw new IllegalArgumentException("Validation failed");
    }

    @GetMapping("/test/illegal-argument")
    public void illegalArgument() {
        throw new IllegalArgumentException("Invalid argument");
    }

    @GetMapping("/test/out-of-stock")
    public void outOfStock() {
        throw new OutOfStockException("Item is out of stock");
    }

    @GetMapping("/test/global-error")
    public void globalError() {
        throw new RuntimeException("Unexpected error");
    }
}
