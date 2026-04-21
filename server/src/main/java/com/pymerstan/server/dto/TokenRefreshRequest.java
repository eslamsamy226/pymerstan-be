package com.pymerstan.server.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TokenRefreshRequest {
    @Schema(description = "Valid refresh token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;
}