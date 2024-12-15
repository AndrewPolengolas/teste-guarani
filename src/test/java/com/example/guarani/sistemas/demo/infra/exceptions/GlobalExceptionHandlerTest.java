package com.example.guarani.sistemas.demo.infra.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void handleResourceNotFoundException() throws Exception {
        mockMvc.perform(get("/test/resource-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void handleValidationExceptions() throws Exception {
        FieldError fieldError = new FieldError("test", "field", "must not be null");
        BindException bindException = new BindException(new Object(), "test");
        bindException.addError(fieldError);

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindException);

        mockMvc.perform(get("/test/validation-error"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void handleIllegalArgumentException() throws Exception {
        mockMvc.perform(get("/test/illegal-argument"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void handleOutOfStockException() throws Exception {
        mockMvc.perform(get("/test/out-of-stock"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("OutOfStock"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void handleGlobalException() throws Exception {
        mockMvc.perform(get("/test/global-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
    }
}
