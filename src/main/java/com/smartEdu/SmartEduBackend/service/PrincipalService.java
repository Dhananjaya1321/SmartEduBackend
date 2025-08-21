package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.enums.Role;
import com.smartEdu.SmartEduBackend.enums.SchoolStatus;
import com.smartEdu.SmartEduBackend.repo.PrincipalRepo;
import com.smartEdu.SmartEduBackend.repo.SchoolRepo;
import com.smartEdu.SmartEduBackend.repo.UserRepo;
import com.smartEdu.SmartEduBackend.repo.ZonalEducationOfficeRepo;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PrincipalService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SchoolRepo schoolRepo;

    @Autowired
    private ZonalEducationOfficeRepo zonalEducationOfficeRepo;

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

    public PrincipalRegisterRequest update(String profileId, PrincipalRegisterRequest updatedPrincipal) {
        Principal principalOld = principalRepo.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Principal not found!"));

        School school = schoolRepo.findById(updatedPrincipal.getSchoolId()).get();
        Principal principal = school.getPrincipal();
        principal.setFullName(updatedPrincipal.getFullName());
        school.setPrincipal(principal);
        schoolRepo.save(school);

        principalOld.setFullName(updatedPrincipal.getFullName());
        principalRepo.save(principalOld);

        User user = userRepo.findByProfileId(profileId);
        user = User.builder()
                .id(user.getId())
                .nic(updatedPrincipal.getNic())
                .contact(updatedPrincipal.getContact())
                .username(user.getUsername())
                .password(user.getPassword())
                .address(updatedPrincipal.getAddress())
                .email(updatedPrincipal.getEmail())
                .role(Role.SCHOOL_ADMIN)
                .name(updatedPrincipal.getFullName())
                .active(true)
                .profileId(profileId)
                .institutionID(user.getInstitutionID())
                .build();

        userRepo.save(user);

        return updatedPrincipal;
    }

    public void delete(String id) {
        principalRepo.findById(id).orElseThrow(() -> new RuntimeException("Principal not found!"));
        principalRepo.deleteById(id);
    }

    public PrincipalResponse findById(String id) {
        Principal principal = principalRepo.findById(id).get();

        return PrincipalResponse.builder()
                .schoolName(schoolRepo.findById(principal.getSchoolId()).get().getSchoolName())
                .fullName(principal.getFullName())
                .build();
    }

    public PrincipalResponse findByIdToSchool(String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username).get();
        Principal principal = principalRepo.findById(user.getProfileId()).get();

        return PrincipalResponse.builder()
                .schoolName(schoolRepo.findById(principal.getSchoolId()).get().getSchoolName())
                .fullName(principal.getFullName())
                .build();
    }

    public Page<PrincipalResponse> findAll(int page, int size, String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);
        ZonalEducationOffice zonalEducationOffice = zonalEducationOfficeRepo.findById(institutionId).orElse(null);

        if (zonalEducationOffice == null) {
            return Page.empty(); // or throw an exception
        }

        List<PrincipalResponse> allPrincipals = new ArrayList<>();
        for (String schoolId : zonalEducationOffice.getSchoolsIds()) {
            Principal principal = principalRepo.findBySchoolId(schoolId).get();
            User user = userRepo.findByProfileId(principal.getId());
            School school = schoolRepo.findById(schoolId).get();

            PrincipalResponse principalRegisterRequest = PrincipalResponse.builder()
                    .id(principal.getId())
                    .schoolId(principal.getSchoolId())
                    .schoolName(school.getSchoolName())
                    .fullName(principal.getFullName())
                    .moeId(principal.getMoeId())
                    .nicFrontImageUrl(principal.getNicFrontImageUrl())
                    .nicBackImageUrl(principal.getNicBackImageUrl())
                    .moeIdFrontImageUrl(principal.getMoeIdFrontImageUrl())
                    .moeIdBackImageUrl(principal.getMoeIdBackImageUrl())
                    .appointmentLetterUrl(principal.getAppointmentLetterUrl())
                    .nic(user.getNic())
                    .contact(user.getContact())
                    .username(user.getUsername())
                    .address(user.getAddress())
                    .email(user.getEmail())
                    .build();

            allPrincipals.add(principalRegisterRequest);
        }

        // Manual Pagination Logic
        int start = page * size;
        int end = Math.min(start + size, allPrincipals.size());

        if (start > end) {
            return Page.empty(); // no content for this page
        }

        List<PrincipalResponse> pagedList = allPrincipals.subList(start, end);
        return new PageImpl<>(pagedList, PageRequest.of(page, size), allPrincipals.size());
    }


    public User getPrincipalUserAccountDetailsByProfileId(String id) {
        return userRepo.findByProfileId(id);
    }
}
