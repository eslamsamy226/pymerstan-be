package com.pymerstan.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateProfileRequest {
    @Schema(description = "User's full name")
    private String name;

    @Email(message = "Email must be a valid format")
    @Schema(description = "User's email address")
    private String email;

    @Schema(description = "User's phone number")
    private String phone;

    @Schema(description = "User's WhatsApp number")
    private String whatsapp;

    @Schema(description = "ID of the user's country")
    private Integer country;

    @Schema(description = "Job title or description")
    private String job;

    @Schema(description = "Application type (Individual/Company)")
    private String type;

    @Schema(description = "Company code if applicable")
    private String code;

    @Schema(description = "New profile photo image file")
    private MultipartFile photo;

    @Schema(description = "New PDF CV document")
    private MultipartFile cv;
}