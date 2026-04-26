package com.pymerstan.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePasswordRequest {
    @NotBlank(message = "Current password is required")
    @Schema(description = "The user's current password")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Schema(description = "The new password to set")
    private String newPassword;
}