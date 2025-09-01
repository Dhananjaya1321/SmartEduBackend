package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.enums.Role;
import com.smartEdu.SmartEduBackend.repo.ProvincialEducationOfficeRepo;
import com.smartEdu.SmartEduBackend.repo.UserRepo;
import com.smartEdu.SmartEduBackend.repo.ZonalEducationOfficeRepo;
import com.smartEdu.SmartEduBackend.util.EmailUtil;
import com.smartEdu.SmartEduBackend.util.PasswordGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ZMOEService {
    @Autowired
    private final ZonalEducationOfficeRepo zmoeRepo;

    @Autowired
    private final ProvincialEducationOfficeRepo provincialEducationOfficeRepo;

    @Autowired
    private final UserRepo userRepo;

    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private final EmailUtil emailUtil;

    public ZonalEducationOffice createZonalEducationOfficeWithUser(ZonalEducationOfficeRequest request) {
        Optional<User> existingUsername = userRepo.findByUsername(request.getUsername());
        if (existingUsername.isPresent())
            throw new RuntimeException("Username is already exists!");

        Optional<User> existingEmail = userRepo.findByEmail(request.getEmail());
        if (existingEmail.isPresent())
            throw new RuntimeException("Email is already exists!");

        ZonalEducationOffice zmoe = ZonalEducationOffice.builder()
                .province(request.getProvince())
                .district(request.getDistrict())
                .zonal(request.getZonal())
                .officeAddress(request.getOfficeAddress())
                .fullName(request.getName())
                .build();

        ZonalEducationOffice savedOffice = zmoeRepo.save(zmoe);

        ProvincialEducationOffice byProvince = provincialEducationOfficeRepo.findByProvince(savedOffice.getProvince());
        byProvince.getZonalOffices().add(savedOffice);
        provincialEducationOfficeRepo.save(byProvince);

        String rawPassword = PasswordGeneratorUtil.generate();

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.ZMOE_ADMIN)
                .email(request.getEmail())
                .nic(request.getNic())
                .contact(request.getContact())
                .address(request.getAddress())
                .active(true)
                .profileId(savedOffice.getId())
                .build();

        userRepo.save(user);

        String subject = "SmartEdu - ZMOE Admin Account Created";
        String message = "Welcome to SmartEdu.\n\nYour ZMOE Admin account has been created.\n" +
                "Username: " + user.getUsername() + "\n" +
                "Temporary Password: " + rawPassword + "\n\nPlease change your password upon first login.";

        emailUtil.sendEmail(user.getEmail(), subject, message);

        return savedOffice;
    }

    public Page<ZonalEducationOffice> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return zmoeRepo.findAll(pageable);
    }
}
