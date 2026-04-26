package com.pymerstan.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SignupRequest {

    @Schema(description = "User's full name", example = "John Doe")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid format")
    @Schema(description = "User's email address", example = "john@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(description = "User's password", example = "securePassword123")
    private String password;

    @Schema(description = "User's phone number", example = "+201234567890")
    private String phone;

    @Schema(description = "User's WhatsApp number", example = "+201234567890")
    private String whatsapp;

    @Schema(description = "User's gender (Male/Female)", example = "Male")
    private String gender;

    @Schema(description = "ID of the user's country", example = "1")
    private Integer country;

    @Schema(description = "Job title or description", example = "Software Engineer")
    private String job;

    @Schema(description = "Application type (Individual/Company)", example = "Individual")
    private String type;

    @Schema(description = "Company code if applicable", example = "COMP123")
    private String code;

    @Schema(description = "Additional message or specific course request")
    private String message;

    @Schema(description = "Profile photo image file")
    private MultipartFile photo;

    @Schema(description = "PDF CV document")
    private MultipartFile cv;
}