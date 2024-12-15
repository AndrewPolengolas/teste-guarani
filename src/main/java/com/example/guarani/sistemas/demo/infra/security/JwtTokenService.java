package com.example.guarani.sistemas.demo.infra.security;

import com.example.guarani.sistemas.demo.app.dto.auth.AuthRequestDTO;
import com.example.guarani.sistemas.demo.domain.model.Role;
import com.example.guarani.sistemas.demo.domain.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class JwtTokenService {

    private final JwtEncoder encoder;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JwtTokenService(JwtEncoder encoder, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.encoder = encoder;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public boolean validateLogin(AuthRequestDTO authRequestDTO, User user) {
        return bCryptPasswordEncoder.matches(authRequestDTO.passWord(), user.getPassword());
    }

    public String generateToken(User user) {

        String scopes = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("orderMS")
                .subject(user.getUserId().toString())
                .expiresAt(Instant.now().plusSeconds(600L))
                .issuedAt(Instant.now())
                .claim("scope", scopes)
                .build();

        return encoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }
}
