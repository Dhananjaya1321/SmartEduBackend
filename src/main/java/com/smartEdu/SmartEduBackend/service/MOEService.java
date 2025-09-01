package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.enums.Role;
import com.smartEdu.SmartEduBackend.repo.MinistryEducationOfficeRepo;
import com.smartEdu.SmartEduBackend.repo.ProvincialEducationOfficeRepo;
import com.smartEdu.SmartEduBackend.repo.UserRepo;
import com.smartEdu.SmartEduBackend.util.EmailUtil;
import com.smartEdu.SmartEduBackend.util.PasswordGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MOEService {
    @Autowired
    private final UserRepo userRepo;

    @Autowired
    private final MinistryEducationOfficeRepo ministryEducationOfficeRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private final EmailUtil emailUtil;

    public MinistryOfEducationOffice createMinistryOfEducationOfficeWithUser(MinistryOfEducationOfficeRequest request) throws Exception {
        // Check if username already exists
        Optional<User> existingUserByUsername = userRepo.findByUsername(request.getUsername());
        if (existingUserByUsername.isPresent())
            throw new RuntimeException("Username is already exists!");

        // Check if email already exists
        Optional<User> existingUserByEmail = userRepo.findByEmail(request.getEmail());
        if (existingUserByEmail.isPresent())
            throw new RuntimeException("Email is already exists!");

        // Save Office
        MinistryOfEducationOffice office = MinistryOfEducationOffice.builder()
                .officeAddress(request.getOfficeAddress())
                .name(request.getName())
                .build();

        MinistryOfEducationOffice savedOffice = ministryEducationOfficeRepo.save(office);

        // Generate random password
        String rawPassword = PasswordGeneratorUtil.generate();

        // Save User
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.MOE_ADMIN)
                .email(request.getEmail())
                .nic(request.getNic())
                .contact(request.getContact())
                .address(request.getAddress())
                .active(true)
                .profileId(savedOffice.getId())
                .build();

        user = userRepo.save(user);

        // Send email with login details
        String subject = "SmartEdu - MOE Admin Account Created";
        String message = "Welcome to SmartEdu.\n\nYour MOE Admin account has been created.\n" +
                "Username: " + user.getUsername() + "\n" +
                "Temporary Password: " + rawPassword + "\n\n" +
                "Please change your password upon first login.";

        emailUtil.sendEmail(user.getEmail(), subject, message);

        return savedOffice;
    }

    public Page<MinistryOfEducationOffice> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ministryEducationOfficeRepo.findAll(pageable);
    }
}
