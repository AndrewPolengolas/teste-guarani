package com.example.guarani.sistemas.demo.app.service;

import com.example.guarani.sistemas.demo.app.dto.auth.AuthRequestDTO;
import com.example.guarani.sistemas.demo.domain.model.EnumRoles;
import com.example.guarani.sistemas.demo.domain.model.Role;
import com.example.guarani.sistemas.demo.domain.model.User;
import com.example.guarani.sistemas.demo.domain.repository.RoleRepository;
import com.example.guarani.sistemas.demo.domain.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public void createUser(AuthRequestDTO authRequestDTO){

        Role role = roleRepository.findByName(EnumRoles.OPERATOR.name());

        Optional<User> user = userRepository.findByUserName(authRequestDTO.userName());

        user.ifPresentOrElse(
                u -> {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
                },
                () -> {
                    User newUser = new User();
                    newUser.setUserName(authRequestDTO.userName());
                    newUser.setPassword(passwordEncoder.encode(authRequestDTO.passWord()));
                    newUser.setRoles(Set.of(role));
                    userRepository.save(newUser);
                }
        );
    }

    public List<User> findAllUsers(){
        return userRepository.findAll();
    }
}
