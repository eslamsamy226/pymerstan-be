package com.pymerstan.server.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StudentResponse {
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
    private LocalDateTime addedDate;
}