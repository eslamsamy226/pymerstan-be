package com.pymerstan.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(example = "john@example.com")
    private String email;

    @NotBlank(message = "OTP is required")
    @Schema(example = "123456")
    private String otp;
}