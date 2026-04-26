package com.pymerstan.server.service;

import com.pymerstan.server.dto.*;
import com.pymerstan.server.entity.Role;
import com.pymerstan.server.entity.User;
import com.pymerstan.server.entity.UserOtp;
import com.pymerstan.server.repository.RoleRepository;
import com.pymerstan.server.repository.UserOtpRepository;
import com.pymerstan.server.repository.UserRepository;
import com.pymerstan.server.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final FileStorageService fileStorageService;
    private final UserOtpRepository userOtpRepository;
    private final EmailService emailService;

    private Map<String, Object> generateCustomClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("name", user.getName());
        claims.put("email", user.getEmail());
        claims.put("isVerified", user.isVerified());
        return claims;
    }

    // --- Core Reusable User Creation Logic ---
    @Transactional
    public User createSystemUser(SignupRequest request, String roleName, boolean isVerified) {
        // 1. Validate File Types
        if (request.getPhoto() != null && !request.getPhoto().isEmpty()) {
            String photoType = request.getPhoto().getContentType();
            if (photoType == null || !photoType.startsWith("image/")) {
                throw new IllegalArgumentException("Uploaded photo must be an image file.");
            }
        }

        if (request.getCv() != null && !request.getCv().isEmpty()) {
            String cvType = request.getCv().getContentType();
            if (cvType == null || !cvType.equals("application/pdf")) {
                throw new IllegalArgumentException("Uploaded CV must be a PDF document.");
            }
        }

        // 2. Validate Uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        // 3. Save Files
        String photoFileName = fileStorageService.savePhoto(request.getPhoto());
        String cvFileName = fileStorageService.saveCv(request.getCv());

        // 4. Resolve Role
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role " + roleName + " not found."));

        // 5. Determine if profile data is completed
        boolean isDataCompleted = request.getName() != null && !request.getName().trim().isEmpty()
                && request.getPhone() != null && !request.getPhone().trim().isEmpty();

        // 6. Map to Entity
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setWhatsapp(request.getWhatsapp());
        user.setGender(request.getGender());
        user.setCountry(request.getCountry());
        user.setOtherJob(request.getJob());
        user.setType(request.getType());
        user.setCode(request.getCode());
        user.setMessage(request.getMessage());
        user.setPhoto(photoFileName);
        user.setCv(cvFileName);

        user.setAddedDate(LocalDateTime.now());
        user.setRoles(Collections.singleton(role));
        user.setVisible(true);
        user.setVerified(isVerified);
        user.setDataCompleted(isDataCompleted);

        return userRepository.save(user);
    }

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        User user = createSystemUser(request, "STUDENT", false);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        Map<String, Object> extraClaims = generateCustomClaims(user);

        String accessToken = jwtService.generateAccessToken(extraClaims, userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        Map<String, Object> extraClaims = generateCustomClaims(user);

        String accessToken = jwtService.generateAccessToken(extraClaims, userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse refresh(String refreshToken) {
        String email = jwtService.extractUsername(refreshToken);

        if (email != null) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                Map<String, Object> extraClaims = generateCustomClaims(user);
                String accessToken = jwtService.generateAccessToken(extraClaims, userDetails);

                return AuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }
        throw new RuntimeException("Invalid refresh token");
    }

    @Transactional
    public String sendVerificationOtp(SendOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.isVerified()) {
            return "User is already verified.";
        }

        String otpCode = generateRandomOtp();

        UserOtp userOtp = userOtpRepository.findByUserId(user.getId())
                .orElse(new UserOtp());

        userOtp.setUser(user);
        userOtp.setOtp(otpCode);
        userOtp.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        userOtpRepository.save(userOtp);

        emailService.sendVerificationOtp(user.getEmail(), otpCode);
        return "OTP sent successfully.";
    }

    @Transactional
    public String verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.isVerified()) {
            return "User is already verified.";
        }

        UserOtp userOtp = userOtpRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("No OTP found for this user."));

        if (userOtp.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP has expired. Please request a new one.");
        }

        if (!userOtp.getOtp().equals(request.getOtp())) {
            throw new IllegalArgumentException("Invalid OTP.");
        }

        user.setVerified(true);
        userRepository.save(user);
        userOtpRepository.delete(userOtp);

        return "User successfully verified.";
    }

    private String generateRandomOtp() {
        int randomPin = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(randomPin);
    }
}