package com.example.guarani.sistemas.demo.app.service;

import com.example.guarani.sistemas.demo.app.dto.auth.AuthRequestDTO;
import com.example.guarani.sistemas.demo.domain.model.EnumRoles;
import com.example.guarani.sistemas.demo.domain.model.Role;
import com.example.guarani.sistemas.demo.domain.model.User;
import com.example.guarani.sistemas.demo.domain.repository.RoleRepository;
import com.example.guarani.sistemas.demo.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUserSuccess() {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("newUser", "password123");
        Role operatorRole = new Role();
        operatorRole.setName(EnumRoles.OPERATOR.name());

        when(roleRepository.findByName(EnumRoles.OPERATOR.name())).thenReturn(operatorRole);
        when(userRepository.findByUserName(authRequestDTO.userName())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(authRequestDTO.passWord())).thenReturn("encodedPassword");

        userService.createUser(authRequestDTO);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserAlreadyExists() {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("existingUser", "password123");
        User existingUser = new User();
        existingUser.setUserName("existingUser");

        when(userRepository.findByUserName(authRequestDTO.userName())).thenReturn(Optional.of(existingUser));

        assertThrows(ResponseStatusException.class, () -> userService.createUser(authRequestDTO));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testFindAllUsers() {
        User user1 = new User();
        user1.setUserName("user1");

        User user2 = new User();
        user2.setUserName("user2");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> users = userService.findAllUsers();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(user -> user.getUserName().equals("user1")));
        assertTrue(users.stream().anyMatch(user -> user.getUserName().equals("user2")));
    }
}
