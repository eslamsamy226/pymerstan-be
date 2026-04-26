package com.pymerstan.server.controller;

import com.pymerstan.server.dto.AdminUpdatePasswordRequest;
import com.pymerstan.server.dto.AdminUpdateUserRequest;
import com.pymerstan.server.dto.SignupRequest;
import com.pymerstan.server.dto.UserResponse;
import com.pymerstan.server.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Endpoints restricted to administrative staff")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Get all students", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllStudents() {
        return ResponseEntity.ok(adminService.getAllStudents());
    }

    @Operation(summary = "Create a new Student", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/students", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> createStudent(@Valid @ModelAttribute SignupRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createStudent(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Create a new Instructor", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/instructors", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> createInstructor(@Valid @ModelAttribute SignupRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createInstructor(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Create a new Admin", description = "Restricted to SUPER_ADMIN only", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/admins", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createAdmin(@Valid @ModelAttribute SignupRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createAdmin(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @Operation(summary = "Get all instructors", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/instructors")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllInstructors() {
        return ResponseEntity.ok(adminService.getAllInstructors());
    }

    @Operation(summary = "Get all admins", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/admins")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    @Operation(summary = "Update user data", description = "Update a specific user's profile. Subject to role hierarchy.", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(value = "/users/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            @Valid @ModelAttribute AdminUpdateUserRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        try {
            return ResponseEntity.ok(adminService.updateUser(userId, request, currentUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Update user password", description = "Force change a user's password. Subject to role hierarchy.", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/users/{userId}/password")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> updateUserPassword(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUpdatePasswordRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        try {
            adminService.updateUserPassword(userId, request, currentUser);
            return ResponseEntity.ok("Password updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}