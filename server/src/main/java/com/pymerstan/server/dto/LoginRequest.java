package com.pymerstan.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginRequest {
    @Schema(description = "User's email address", example = "john@example.com")
    private String email;

    @Schema(description = "User's password", example = "securePassword123")
    private String password;
}