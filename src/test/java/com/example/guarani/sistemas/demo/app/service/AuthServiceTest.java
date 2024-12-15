package com.example.guarani.sistemas.demo.app.service;

import com.example.guarani.sistemas.demo.app.dto.auth.AuthRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.auth.AuthResponseDTO;
import com.example.guarani.sistemas.demo.domain.model.User;
import com.example.guarani.sistemas.demo.domain.repository.UserRepository;
import com.example.guarani.sistemas.demo.infra.security.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateLoginSuccess() {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("validUser", "validPassword");
        User user = new User();
        user.setUserName("validUser");
        user.setPassword("hashedPassword");

        when(userRepository.findByUserName(authRequestDTO.userName())).thenReturn(Optional.of(user));
        when(jwtTokenService.validateLogin(authRequestDTO, user)).thenReturn(true);
        when(jwtTokenService.generateToken(user)).thenReturn("validToken");

        AuthResponseDTO response = authService.validateLogin(authRequestDTO);

        assertNotNull(response);
        assertEquals("validToken", response.acessToken());

        verify(userRepository, times(1)).findByUserName(authRequestDTO.userName());
        verify(jwtTokenService, times(1)).validateLogin(authRequestDTO, user);
        verify(jwtTokenService, times(1)).generateToken(user);
    }

    @Test
    void testValidateLoginInvalidUser() {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("invalidUser", "password");

        when(userRepository.findByUserName(authRequestDTO.userName())).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.validateLogin(authRequestDTO));

        verify(userRepository, times(1)).findByUserName(authRequestDTO.userName());
        verify(jwtTokenService, never()).validateLogin(any(), any());
        verify(jwtTokenService, never()).generateToken(any());
    }

    @Test
    void testValidateLoginInvalidPassword() {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("validUser", "invalidPassword");
        User user = new User();
        user.setUserName("validUser");
        user.setPassword("hashedPassword");

        when(userRepository.findByUserName(authRequestDTO.userName())).thenReturn(Optional.of(user));
        when(jwtTokenService.validateLogin(authRequestDTO, user)).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.validateLogin(authRequestDTO));

        verify(userRepository, times(1)).findByUserName(authRequestDTO.userName());
        verify(jwtTokenService, times(1)).validateLogin(authRequestDTO, user);
        verify(jwtTokenService, never()).generateToken(any());
    }
}
