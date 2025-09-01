package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;

import com.smartEdu.SmartEduBackend.enums.Role;
import com.smartEdu.SmartEduBackend.repo.PrincipalRepo;
import com.smartEdu.SmartEduBackend.repo.SchoolRepo;
import com.smartEdu.SmartEduBackend.repo.UserRepo;
import com.smartEdu.SmartEduBackend.repo.ZonalEducationOfficeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SchoolService {

    @Autowired
    private SchoolRepo schoolRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PrincipalRepo principalRepo;

    @Autowired
    private ZonalEducationOfficeRepo zonalEducationOfficeRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public School saveSchoolWithPrincipal(SchoolRequest request) {
        // Check if username already exists
        Optional<User> existingUserByUsername = userRepo.findByUsername(request.getUsername());
        if (existingUserByUsername.isPresent())
            throw new RuntimeException("Username is already exists!");

        // Check if email already exists
        Optional<User> existingUserByEmail = userRepo.findByEmail(request.getEmail());
        if (existingUserByEmail.isPresent())
            throw new RuntimeException("Email is already exists!");

        Principal principal = request.getPrincipal();
        Principal savedPrincipal = principalRepo.save(principal);

        School school = request.getSchool();
        school.setSchoolNumber(generateSchoolNumber());
        school.setPrincipal(savedPrincipal);
        School savedSchool = schoolRepo.save(school);

        ZonalEducationOffice zonal = zonalEducationOfficeRepo.findByZonalAndDistrictAndProvince(
                savedSchool.getZonal(),
                savedSchool.getDistrict(),
                savedSchool.getProvince()
        );
        zonal.getSchools().add(savedSchool);
        zonalEducationOfficeRepo.save(zonal);

        savedPrincipal.setSchoolId(savedSchool.getId());
        principalRepo.save(savedPrincipal);

        User user = User.builder()
                .nic(request.getNic())
                .contact(request.getContact())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .address(request.getAddress())
                .email(request.getEmail())
                .role(Role.SCHOOL_ADMIN)
                .active(true)
                .profileId(savedPrincipal.getId())
                .build();

        userRepo.save(user);

        return savedSchool;
    }

    // Update existing school (and optionally principal)
    public School update(String id, School updatedSchool) {
        schoolRepo.findById(id).orElseThrow(() -> new RuntimeException("School not found!"));
        updatedSchool.setPrincipal(principalRepo.findBySchoolId(id).get());
        updatedSchool.setId(id);
        return schoolRepo.save(updatedSchool);
    }

    // Delete school by ID
    public void delete(String id) {
        schoolRepo.findById(id).orElseThrow(() -> new RuntimeException("School not found!"));
        schoolRepo.deleteById(id);
    }

    // Find by ID
    public Optional<School> findById(String id) {
        return schoolRepo.findById(id);
    }

    // Find all
    public Page<School> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return schoolRepo.findAll(pageable);
    }

    public String generateSchoolNumber() {
        long count = schoolRepo.count(); // total registered schools
        long nextNumber = count + 1;
        return String.format("SCH-%05d", nextNumber);
    }

}
