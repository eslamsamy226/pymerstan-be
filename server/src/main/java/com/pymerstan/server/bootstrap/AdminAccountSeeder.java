package com.pymerstan.server.bootstrap;

import com.pymerstan.server.entity.Role;
import com.pymerstan.server.entity.User;
import com.pymerstan.server.repository.RoleRepository;
import com.pymerstan.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminAccountSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminAccountSeeder.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Check if a SUPER_ADMIN already exists in the database
        List<User> superAdmins = userRepository.findByRoles_Name("SUPER_ADMIN");

        if (superAdmins.isEmpty()) {
            log.info("No Super Admin found. Creating default Super Admin account...");

            Role superAdminRole = roleRepository.findByName("SUPER_ADMIN")
                    .orElseThrow(() -> new RuntimeException("SUPER_ADMIN role not found. Ensure Liquibase migrations have run."));

            User admin = new User();
            admin.setEmail("superadmin@pymerstan.com");
            // Encodes "123456" using your custom MD5 encoder
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setName("System Super Admin");

            // Fill required NOT NULL columns with placeholder data
            admin.setPhone("00000000000");
            admin.setAddedDate(LocalDateTime.now());
            admin.setVerified(true); // Automatically verified
            admin.setDataCompleted(true);
            admin.setVisible(true);

            admin.setRoles(Collections.singleton(superAdminRole));

            userRepository.save(admin);
            log.info("Default Super Admin created successfully: superadmin@pymerstan.com / 123456");
        } else {
            log.info("Super Admin account already exists. Skipping initialization.");
        }
    }
}