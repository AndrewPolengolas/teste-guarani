package com.example.guarani.sistemas.demo.infra.config;

import com.example.guarani.sistemas.demo.domain.model.EnumRoles;
import com.example.guarani.sistemas.demo.domain.model.Role;
import com.example.guarani.sistemas.demo.domain.model.User;
import com.example.guarani.sistemas.demo.domain.repository.RoleRepository;
import com.example.guarani.sistemas.demo.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Set;

@Configuration
public class UserAdminConfig implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserAdminConfig(RoleRepository roleRepository, UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        Role role = roleRepository.findByName(EnumRoles.ADMIN.name());
        Optional<User> admin = userRepository.findByUserName("admin");

        admin.ifPresentOrElse(
                u -> {
                    System.out.println("user already exists");
                },
                () -> {
                    User user = new User();
                    user.setUserName("admin");
                    user.setPassword(encoder.encode("123"));
                    user.setRoles(Set.of(role));
                    userRepository.save(user);
                }
        );
    }
}
