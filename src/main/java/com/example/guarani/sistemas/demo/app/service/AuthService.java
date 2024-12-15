package com.example.guarani.sistemas.demo.app.service;

import com.example.guarani.sistemas.demo.app.dto.auth.AuthRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.auth.AuthResponseDTO;
import com.example.guarani.sistemas.demo.domain.model.User;
import com.example.guarani.sistemas.demo.domain.repository.UserRepository;
import com.example.guarani.sistemas.demo.infra.security.JwtTokenService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;

    public AuthService(UserRepository userRepository, JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.jwtTokenService = jwtTokenService;
    }

    public AuthResponseDTO validateLogin(AuthRequestDTO authRequestDTO) {
        User user = userRepository.findByUserName(authRequestDTO.userName()).orElseThrow(() -> new BadCredentialsException("Invalid user or password"));

        if (!jwtTokenService.validateLogin(authRequestDTO, user)){
            throw new BadCredentialsException("Invalid user or password");
        }

        return new AuthResponseDTO(jwtTokenService.generateToken(user));
    }
}
