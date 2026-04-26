package com.pymerstan.server.service;

import com.pymerstan.server.dto.UpdatePasswordRequest;
import com.pymerstan.server.dto.UpdateProfileRequest;
import com.pymerstan.server.dto.UserResponse;
import com.pymerstan.server.entity.User;
import com.pymerstan.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserResponse.fromEntity(user);
    }

    @Transactional
    public UserResponse updateProfile(String currentEmail, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Handle Email Update Logic
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("The requested email is already in use by another account.");
            }
            user.setEmail(request.getEmail());
            // Note: If the user changes their email, their current JWT token will become invalid
            // on the next request because the email inside it won't match the database anymore.
            // The frontend should force a re-login if the email changes.
        }

        // Update Text Fields (Ignoring nulls, allowing empty strings to clear data if desired)
        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getWhatsapp() != null) user.setWhatsapp(request.getWhatsapp());
        if (request.getCountry() != null) user.setCountry(request.getCountry());
        if (request.getJob() != null) user.setOtherJob(request.getJob());
        if (request.getType() != null) user.setType(request.getType());
        if (request.getCode() != null) user.setCode(request.getCode());

        // Update Data Completed Flag (Re-evaluate based on essential fields)
        boolean isDataCompleted = user.getName() != null && !user.getName().trim().isEmpty()
                && user.getPhone() != null && !user.getPhone().trim().isEmpty();
        user.setDataCompleted(isDataCompleted);

        // Handle File Uploads (Abandoned old files remain on disk for now)
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
    public void updatePassword(String email, UpdatePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("The current password provided is incorrect.");
        }

        // Apply new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}