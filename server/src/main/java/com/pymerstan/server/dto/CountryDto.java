package com.pymerstan.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CountryDto {

    @NotNull(message = "Country ID is required")
    @Schema(description = "Numeric ID of the country", example = "19")
    private Integer id;

    @NotBlank(message = "Country name is required")
    @Schema(description = "Name of the country", example = "Canada")
    private String name;

    @NotNull(message = "Visibility status is required")
    @Schema(description = "Whether this country is visible in the frontend dropdown", example = "true")
    private Boolean visible;
}