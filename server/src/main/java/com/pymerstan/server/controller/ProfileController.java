package com.pymerstan.server.controller;

import com.pymerstan.server.dto.UpdatePasswordRequest;
import com.pymerstan.server.dto.UpdateProfileRequest;
import com.pymerstan.server.dto.UserResponse;
import com.pymerstan.server.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "Endpoints for users to manage their own profile data")
public class ProfileController {

    private final ProfileService profileService;

    @Operation(summary = "Get my profile", description = "Retrieves the profile of the currently logged-in user", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(profileService.getProfile(userDetails.getUsername()));
    }

    @Operation(summary = "Update my profile", description = "Updates general profile information including files", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute UpdateProfileRequest request) {
        try {
            UserResponse updatedProfile = profileService.updateProfile(userDetails.getUsername(), request);
            return ResponseEntity.ok(updatedProfile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Change my password", description = "Requires current password verification to set a new password", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/me/password")
    public ResponseEntity<String> updateMyPassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdatePasswordRequest request) {
        try {
            profileService.updatePassword(userDetails.getUsername(), request);
            return ResponseEntity.ok("Password updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}