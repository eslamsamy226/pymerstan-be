package com.pymerstan.server.service;

import com.pymerstan.server.dto.AdminUpdatePasswordRequest;
import com.pymerstan.server.dto.AdminUpdateUserRequest;
import com.pymerstan.server.dto.SignupRequest;
import com.pymerstan.server.dto.UserResponse;
import com.pymerstan.server.entity.Role;
import com.pymerstan.server.entity.User;
import com.pymerstan.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AuthService authService;
    // Add these new dependencies
    private final FileStorageService fileStorageService;
    private final PasswordEncoder passwordEncoder;

    // --- Security Hierarchy Helper ---
    private void verifyAdminHierarchy(UserDetails currentUser, User targetUser) {
        boolean isSuperAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (!isSuperAdmin) {
            boolean targetIsAdminOrSuper = targetUser.getRoles().stream()
                    .anyMatch(r -> r.getName().equals("ADMIN") || r.getName().equals("SUPER_ADMIN"));

            if (targetIsAdminOrSuper) {
                throw new AccessDeniedException("You do not have permission to modify an Administrator's data.");
            }
        }
    }
    public List<UserResponse> getAllStudents() {
        return userRepository.findByRoles_Name("STUDENT").stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getAllInstructors() {
        return userRepository.findByRoles_Name("INSTRUCTOR").stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    public List<UserResponse> getAllAdmins() {
        // Fetch users who have either ADMIN or SUPER_ADMIN roles
        return userRepository.findByRoles_NameIn(List.of("ADMIN", "SUPER_ADMIN")).stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public UserResponse createStudent(SignupRequest request) {
        User user = authService.createSystemUser(request, "STUDENT", true);
        return mapToResponse(user);
    }

    public UserResponse createInstructor(SignupRequest request) {
        User user = authService.createSystemUser(request, "INSTRUCTOR", true);
        return mapToResponse(user);
    }

    public UserResponse createAdmin(SignupRequest request) {
        User user = authService.createSystemUser(request, "ADMIN", true);
        return mapToResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long userId, AdminUpdateUserRequest request, UserDetails currentUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        verifyAdminHierarchy(currentUser, user);

        // Handle Email Update
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("The requested email is already in use.");
            }
            user.setEmail(request.getEmail());
        }

        // Update standard text fields
        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getWhatsapp() != null) user.setWhatsapp(request.getWhatsapp());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getCountry() != null) user.setCountry(request.getCountry());
        if (request.getJob() != null) user.setOtherJob(request.getJob());
        if (request.getType() != null) user.setType(request.getType());
        if (request.getCode() != null) user.setCode(request.getCode());
        if (request.getMessage() != null) user.setMessage(request.getMessage());

        // Update Admin-only booleans
        if (request.getIsVisible() != null) user.setVisible(request.getIsVisible());
        if (request.getIsVerified() != null) user.setVerified(request.getIsVerified());

        // Re-evaluate data completion
        boolean isDataCompleted = user.getName() != null && !user.getName().trim().isEmpty()
                && user.getPhone() != null && !user.getPhone().trim().isEmpty();
        user.setDataCompleted(isDataCompleted);

        // Handle File Uploads
        if (request.getPhoto() != null && !request.getPhoto().isEmpty()) {
            String photoType = request.getPhoto().getContentType();
            if (photoType == null || !photoType.startsWith("image/")) {
                throw new IllegalArgumentException("Uploaded photo must be an image file.");
            }
            user.setPhoto(fileStorageService.savePhoto(request.getPhoto()));
        }

        if (request.getCv() != null && !request.getCv().isEmpty()) {
            String cvType = request.getCv().getContentType();
            if (cvType == null || !cvType.equals("application/pdf")) {
                throw new IllegalArgumentException("Uploaded CV must be a PDF document.");
            }
            user.setCv(fileStorageService.saveCv(request.getCv()));
        }

        user = userRepository.save(user);
        return UserResponse.fromEntity(user);
    }

    @Transactional
    public void updateUserPassword(Long userId, AdminUpdatePasswordRequest request, UserDetails currentUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        verifyAdminHierarchy(currentUser, user);

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // Helper mapper to reuse mapping logic
    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .whatsapp(user.getWhatsapp())
                .gender(user.getGender())
                .country(user.getCountry())
                .otherJob(user.getOtherJob())
                .type(user.getType())
                .code(user.getCode())
                .photo(user.getPhoto())
                .cv(user.getCv())
                .isVerified(user.isVerified())
                .isDataCompleted(user.isDataCompleted())
                .visible(user.isVisible()) // Map the visible property
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList())) // Map the roles into a List of Strings
                .addedDate(user.getAddedDate())
                .build();
    }
}