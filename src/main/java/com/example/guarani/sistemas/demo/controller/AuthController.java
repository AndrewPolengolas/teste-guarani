package com.example.guarani.sistemas.demo.controller;

import com.example.guarani.sistemas.demo.app.dto.auth.AuthRequestDTO;
import com.example.guarani.sistemas.demo.app.dto.auth.AuthResponseDTO;
import com.example.guarani.sistemas.demo.app.service.AuthService;
import com.example.guarani.sistemas.demo.app.service.UserService;
import com.example.guarani.sistemas.demo.domain.model.User;
import jakarta.transaction.Transactional;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for authentication and user management")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login",
            description = "Validates user credentials and returns an authentication token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                            content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials")
            }
    )
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO authRequestDTO) {
        AuthResponseDTO responseDTO = authService.validateLogin(authRequestDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @Transactional
    @PostMapping("/users")
    @Operation(
            summary = "Create a new user",
            description = "Registers a new user with the provided data.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User successfully created"),
                    @ApiResponse(responseCode = "400", description = "Invalid data")
            }
    )
    public ResponseEntity<?> newUser(@RequestBody AuthRequestDTO authRequestDTO) {
        userService.createUser(authRequestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Operation(
            summary = "List all users",
            description = "Returns a list of all registered users. Requires admin permissions.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully returned the user list",
                            content = @Content(schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            }
    )
    public ResponseEntity<?> listUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }
}
