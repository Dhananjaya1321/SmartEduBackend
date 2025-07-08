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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PMOEService {
    @Autowired
    private final ProvincialEducationOfficeRepo provincialEducationOfficeRepo;

    @Autowired
    private final MinistryEducationOfficeRepo ministryEducationOfficeRepo;

    @Autowired
    private final UserRepo userRepo;

    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private final EmailUtil emailUtil;

    public ProvincialEducationOffice createProvincialEducationOfficeWithUser(ProvincialEducationOfficeRequest request) throws Exception {
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
                .officeAddress(request.getOfficeAddress())
                .name(request.getName())
                .build();

        ProvincialEducationOffice savedOffice = provincialEducationOfficeRepo.save(office);

        MinistryOfEducationOffice ministry = ministryEducationOfficeRepo.findAll().get(0);
        ministry.getProvincialOffices().add(savedOffice);
        ministryEducationOfficeRepo.save(ministry);

        // Generate random password
        String rawPassword = PasswordGeneratorUtil.generate();

        // Save User
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.PMOE_ADMIN)
                .name(request.getName())
                .email(request.getEmail())
                .nic(request.getNic())
                .contact(request.getContact())
                .address(request.getAddress())
                .active(true)
                .institutionID(savedOffice.getId())
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

    public Page<ProvincialEducationOfficeAdminResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Get paginated users with role PMOE_ADMIN
        List<User> pagedUsers = userRepo.findAllByRole(pageable, Role.PMOE_ADMIN);

        // Total count of PMOE_ADMIN users (needed for PageImpl)
        long total = userRepo.countByRole(Role.PMOE_ADMIN);

        List<ProvincialEducationOfficeAdminResponse> adminResponses = new ArrayList<>();

        for (User u : pagedUsers) {
            ProvincialEducationOffice office = provincialEducationOfficeRepo.findById(u.getInstitutionID())
                    .orElse(null);

            if (office != null) {
                ProvincialEducationOfficeAdminResponse admin = ProvincialEducationOfficeAdminResponse.builder()
                        .id(u.getId())
                        .contact(u.getContact())
                        .nic(u.getNic())
                        .username(u.getUsername())
                        .address(u.getAddress())
                        .name(u.getName())
                        .role(u.getRole())
                        .email(u.getEmail())
                        .province(office.getProvince())
                        .officeAddress(office.getOfficeAddress())
                        .build();

                adminResponses.add(admin);
            }
        }

        return new PageImpl<>(adminResponses, pageable, total);
    }

}
