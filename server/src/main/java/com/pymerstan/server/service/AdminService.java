package com.pymerstan.server.service;

import com.pymerstan.server.dto.StudentResponse;
import com.pymerstan.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public List<StudentResponse> getAllStudents() {
        return userRepository.findByRoles_Name("STUDENT").stream()
                .map(user -> StudentResponse.builder()
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
                        .addedDate(user.getAddedDate())
                        .build())
                .collect(Collectors.toList());
    }
}