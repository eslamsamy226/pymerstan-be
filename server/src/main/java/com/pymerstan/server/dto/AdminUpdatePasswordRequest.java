package com.pymerstan.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminUpdatePasswordRequest {

    @NotBlank(message = "New password is required")
    @Schema(description = "The new password to set for the user")
    private String newPassword;
}