package com.pymerstan.server.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    @Schema(description = "Short-lived JWT Access Token")
    private String accessToken;

    @Schema(description = "Long-lived JWT Refresh Token")
    private String refreshToken;
}