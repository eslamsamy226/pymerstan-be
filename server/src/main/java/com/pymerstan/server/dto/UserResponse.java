package com.pymerstan.server.dto;

import com.pymerstan.server.entity.Role;
import com.pymerstan.server.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String whatsapp;
    private String gender;
    private Integer country;
    private String otherJob;
    private String type;
    private String code;
    private String photo;
    private String cv;
    private boolean isVerified;
    private boolean isDataCompleted;
    private boolean visible;
    private List<String> roles;
    private LocalDateTime addedDate;

    // Centralized mapping logic to be reused across all services
    public static UserResponse fromEntity(User user) {
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
                .visible(user.isVisible())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .addedDate(user.getAddedDate())
                .build();
    }
}