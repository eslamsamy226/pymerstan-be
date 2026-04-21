package com.pymerstan.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Core & Auth Fields ---
    @Column(unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String photo;

    @Column(name = "added_date", nullable = false)
    private LocalDateTime addedDate;

    // --- Legacy Name Conflict Fields ---
    private String name;

    private String fname;

    // --- Legacy Users Table Fields ---
    private String address;

    @Column(length = 50)
    private String type;

    // --- Legacy Students Table Fields ---
    @Column(name = "job_id")
    private Integer jobId;

    @Column(name = "other_job", length = 50)
    private String otherJob;

    @Column(length = 50)
    private String gender;

    private String code;

    @Column(length = 50)
    private String whatsapp;

    private Integer country;

    private String message;

    @Column(length = 50)
    private String password2;

    // --- Legacy Lecturer & Student Shared Fields ---
    @Column(columnDefinition = "TEXT")
    private String cv;

    // --- Legacy Lecturer Table Fields ---
    @Column(name = "course_found_or_no", length = 10)
    private String courseFoundOrNo;

    @Column(name = "course_id")
    private String courseId;

    @Column(name = "accept_no", length = 50)
    private String acceptNo;

    @Column(name = "visiable", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean visible;

    // --- New Custom Workflow Fields ---
    @Column(name = "is_verified", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isVerified;

    @Column(name = "is_data_completed", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isDataCompleted;
    // --- Relationships ---

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}