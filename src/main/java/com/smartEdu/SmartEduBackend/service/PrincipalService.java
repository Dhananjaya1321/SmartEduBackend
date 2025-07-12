package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.Principal;
import com.smartEdu.SmartEduBackend.entity.PrincipalRegisterRequest;
import com.smartEdu.SmartEduBackend.entity.School;
import com.smartEdu.SmartEduBackend.entity.User;
import com.smartEdu.SmartEduBackend.enums.Role;
import com.smartEdu.SmartEduBackend.repo.PrincipalRepo;
import com.smartEdu.SmartEduBackend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PrincipalService {

    @Autowired
    private PrincipalRepo principalRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    public Principal registerPrincipalWithUser(PrincipalRegisterRequest request) {
        // Check if username already exists
        Optional<User> existingUserByUsername = userRepo.findByUsername(request.getUsername());
        if (existingUserByUsername.isPresent())
            throw new RuntimeException("Username is already exists!");

        // Check if email already exists
        Optional<User> existingUserByEmail = userRepo.findByEmail(request.getEmail());
        if (existingUserByEmail.isPresent())
            throw new RuntimeException("Email is already exists!");

        // Save principal
        Principal principal = Principal.builder()
                .schoolId(request.getSchoolId())
                .fullName(request.getFullName())
                .moeId(request.getMoeId())
                .nicFrontImageUrl(request.getNicFrontImageUrl())
                .nicBackImageUrl(request.getNicBackImageUrl())
                .moeIdFrontImageUrl(request.getMoeIdFrontImageUrl())
                .moeIdBackImageUrl(request.getMoeIdBackImageUrl())
                .appointmentLetterUrl(request.getAppointmentLetterUrl())
                .build();

        Principal savedPrincipal = principalRepo.save(principal);

        // Create user and link profileId
        User user = User.builder()
                .nic(request.getNic())
                .contact(request.getContact())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .address(request.getAddress())
                .email(request.getEmail())
                .role(Role.SCHOOL_ADMIN)
                .name(request.getFullName())
                .active(true)
                .profileId(savedPrincipal.getId())
                .institutionID(request.getSchoolId())
                .build();

        userRepo.save(user);

        return savedPrincipal;
    }

    public Principal update(String id, Principal updatedPrincipal) {
        principalRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Principal not found!"));

        updatedPrincipal.setId(id);
        return principalRepo.save(updatedPrincipal);
    }

    public void delete(String id) {
        principalRepo.findById(id).orElseThrow(() -> new RuntimeException("Principal not found!"));
        principalRepo.deleteById(id);
    }

    public Optional<Principal> findById(String id) {
        return principalRepo.findById(id);
    }

    public Page<Principal> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return principalRepo.findAll(pageable);
    }

    public User getPrincipalUserAccountDetailsByProfileId(String id) {
        return userRepo.findByProfileId(id);
    }
}
