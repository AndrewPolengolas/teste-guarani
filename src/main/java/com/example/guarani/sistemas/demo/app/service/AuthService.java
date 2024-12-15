package com.example.guarani.sistemas.demo.app.service;

import com.example.guarani.sistemas.demo.app.dto.auth.AuthRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.auth.AuthResponseDTO;
import com.example.guarani.sistemas.demo.domain.model.User;
import com.example.guarani.sistemas.demo.domain.repository.UserRepository;
import com.example.guarani.sistemas.demo.infra.security.JwtTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;

    public AuthService(UserRepository userRepository, JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.jwtTokenService = jwtTokenService;
    }

    public AuthResponseDTO validateLogin(AuthRequestDTO authRequestDTO) {
        logger.info("Starting login validation for user: {}", authRequestDTO.userName());

        User user = userRepository.findByUserName(authRequestDTO.userName())
                .orElseThrow(() -> {
                    logger.warn("User not found: {}", authRequestDTO.userName());
                    return new BadCredentialsException("Invalid user or password");
                });

        if (!jwtTokenService.validateLogin(authRequestDTO, user)) {
            logger.warn("Login validation failed for user: {}", authRequestDTO.userName());
            throw new BadCredentialsException("Invalid user or password");
        }

        String token = jwtTokenService.generateToken(user);
        logger.info("Login successful for user: {}, token generated", authRequestDTO.userName());

        return new AuthResponseDTO(token);
    }
}
