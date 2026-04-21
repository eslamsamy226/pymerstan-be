package com.pymerstan.server.controller;

import com.pymerstan.server.dto.StudentResponse;
import com.pymerstan.server.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Endpoints restricted to administrative staff")
// This ensures that EVERY endpoint in this controller requires ADMIN or SUPER_ADMIN
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Operation(
            summary = "Get all students",
            description = "Retrieves a list of all registered students. Restricted to ADMIN and SUPER_ADMIN roles.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/students")
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        return ResponseEntity.ok(adminService.getAllStudents());
    }
}