package com.pymerstan.server.controller;

import com.pymerstan.server.dto.CountryDto;
import com.pymerstan.server.service.CountryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Countries", description = "Endpoints for managing system countries")
public class CountryController {

    private final CountryService countryService;

    // --- PUBLIC ENDPOINT ---

    @Operation(summary = "Get visible countries", description = "Public endpoint to load active countries for frontend dropdowns")
    @GetMapping("/api/countries")
    public ResponseEntity<List<CountryDto>> getVisibleCountries() {
        return ResponseEntity.ok(countryService.getVisibleCountries());
    }

    // --- SECURE ADMIN ENDPOINTS ---

    @Operation(summary = "Get all countries", description = "Admin view of all countries (both visible and hidden)", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/api/admin/countries")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<CountryDto>> getAllCountries() {
        return ResponseEntity.ok(countryService.getAllCountries());
    }

    @Operation(summary = "Add a new country", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/api/admin/countries")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> createCountry(@Valid @RequestBody CountryDto request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(countryService.createCountry(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Update a country", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/api/admin/countries/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> updateCountry(@PathVariable Integer id, @Valid @RequestBody CountryDto request) {
        try {
            return ResponseEntity.ok(countryService.updateCountry(id, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Delete a country", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/api/admin/countries/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> deleteCountry(@PathVariable Integer id) {
        try {
            countryService.deleteCountry(id);
            return ResponseEntity.ok("Country deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}