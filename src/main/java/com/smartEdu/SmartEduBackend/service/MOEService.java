package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.ProvincialEducationOffice;
import com.smartEdu.SmartEduBackend.entity.ProvincialEducationOfficeRequest;
import com.smartEdu.SmartEduBackend.entity.User;
import com.smartEdu.SmartEduBackend.enums.Role;
import com.smartEdu.SmartEduBackend.repo.ProvincialEducationOfficeRepo;
import com.smartEdu.SmartEduBackend.repo.UserRepo;
import com.smartEdu.SmartEduBackend.util.EmailUtil;
import com.smartEdu.SmartEduBackend.util.PasswordGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MOEService {
    @Autowired
    private final UserRepo userRepo;

    @Autowired
    private final ProvincialEducationOfficeRepo pmoeRepo;

    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EmailUtil emailUtil;

    public ProvincialEducationOffice createWithUser(ProvincialEducationOfficeRequest request) throws Exception {
        // Check if username already exists
        Optional<User> existingUserByUsername = userRepo.findByUsername(request.getUsername());
        if (existingUserByUsername.isPresent())
            throw new RuntimeException("Username is already exists!");

        // Check if email already exists
        Optional<User> existingUserByEmail = userRepo.findByEmail(request.getEmail());
        if (existingUserByEmail.isPresent())
            throw new RuntimeException("Email is already exists!");

        // Save Office
        ProvincialEducationOffice office = ProvincialEducationOffice.builder()
                .province(request.getProvince())
                .PEOAddress(request.getPEOAddress())
                .name(request.getName())
                .build();

        ProvincialEducationOffice savedOffice = pmoeRepo.save(office);

        // Generate random password
        String rawPassword = PasswordGeneratorUtil.generate();

        // Save User
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.PMOE_ADMIN)
                .email(request.getEmail())
                .nic(request.getNic())
                .contact(request.getContact())
                .address(request.getAddress())
                .active(true)
                .profileId(savedOffice.getId())
                .build();

        user = userRepo.save(user);

        // Send email with login details
        String subject = "SmartEdu - PMOE Admin Account Created";
        String message = "Welcome to SmartEdu.\n\nYour PMOE Admin account has been created.\n" +
                "Username: " + user.getUsername() + "\n" +
                "Temporary Password: " + rawPassword + "\n\n" +
                "Please change your password upon first login.";

        emailUtil.sendEmail(user.getEmail(), subject, message);

        return savedOffice;
    }
}
