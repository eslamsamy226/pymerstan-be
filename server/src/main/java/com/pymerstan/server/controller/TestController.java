package com.pymerstan.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test", description = "Endpoints to test authentication and authorization")
public class TestController {

    @Operation(
            summary = "Get current logged-in user",
            description = "Requires a valid JWT access token. Returns the email and roles extracted from the token/database.",
            security = @SecurityRequirement(name = "bearerAuth") // Tells Swagger to require a token
    )
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {

        Map<String, Object> userData = new HashMap<>();

        // In Spring Security, the 'username' field holds the email because of our CustomUserDetailsService
        userData.put("email", userDetails.getUsername());

        // Extract roles from GrantedAuthorities
        userData.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return ResponseEntity.ok(userData);
    }
}