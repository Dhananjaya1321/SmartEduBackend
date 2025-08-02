package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;

import com.smartEdu.SmartEduBackend.enums.Role;
import com.smartEdu.SmartEduBackend.enums.SchoolStatus;
import com.smartEdu.SmartEduBackend.repo.*;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
public class SchoolService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SchoolRepo schoolRepo;

    @Autowired
    private GradesRepo gradesRepo;

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

        School school = School.builder()
                .schoolNumber(generateSchoolNumber())
                .schoolName(request.getSchoolName())
                .logoUrl(request.getLogoUrl())
                .province(request.getProvince())
                .district(request.getDistrict())
                .zonal(request.getZonal())
                .levelOfSchool(request.getSchoolLevel())
                .typeOfSchool(request.getSchoolType())
                .gradeSpan(request.getGradeSpan())
                .gender(request.getGender())
                .ethnicity(request.getEthnicity())
                .languageMedium(request.getLanguageMedium())
                .studentPopulation(request.getStudentPopulation())
                .teacherPopulation(request.getTeacherPopulation())
                .classCount(request.getClassCount())
                .status(SchoolStatus.PENDING)
                .build();
        School savedSchool = schoolRepo.save(school);

        Principal principal = Principal.builder()
                .schoolId(savedSchool.getId())
                .fullName(request.getFullName())
                .nicFrontImageUrl(request.getNicFront())
                .nicBackImageUrl(request.getNicBack())
                .moeIdFrontImageUrl(request.getMoeFront())
                .moeIdBackImageUrl(request.getMoeBack())
                .appointmentLetterUrl(request.getAppointment())
                .build();
        Principal savedPrincipal = principalRepo.save(principal);
        savedSchool.setPrincipal(savedPrincipal);
        savedSchool = schoolRepo.save(school);

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
                .institutionID(savedSchool.getId())
                .build();
        userRepo.save(user);


        ZonalEducationOffice zonal = zonalEducationOfficeRepo.findByZonalAndDistrictAndProvince(
                request.getZonal(),
                request.getDistrict(),
                request.getProvince()
        );
        zonal.getSchoolsIds().add(savedSchool.getId());
        zonalEducationOfficeRepo.save(zonal);

        return savedSchool;
    }

    public School update(String id, School updatedSchool) {
        schoolRepo.findById(id).orElseThrow(() -> new RuntimeException("School not found!"));
        updatedSchool.setPrincipal(principalRepo.findBySchoolId(id).get());
        updatedSchool.setId(id);
        return schoolRepo.save(updatedSchool);
    }

    public School updateSchoolStatus(String id, SchoolStatus status) {
        School school = schoolRepo.findById(id).orElseThrow(() -> new RuntimeException("School not found!"));
        school.setStatus(status);
        return schoolRepo.save(school);
    }

    // Delete school by ID
    public void delete(String id,String token) {
        schoolRepo.findById(id).orElseThrow(() -> new RuntimeException("School not found!"));
        String institutionId = jwtUtil.extractInstitutionId(token);

        ZonalEducationOffice zonalEducationOffice = zonalEducationOfficeRepo.findById(institutionId).get();
        zonalEducationOffice.getSchoolsIds().remove(id);
        zonalEducationOfficeRepo.save(zonalEducationOffice);

        schoolRepo.deleteById(id);
    }

    // Find by ID
    public Optional<School> findById(String id) {
        return schoolRepo.findById(id);
    }


    public List<School> findAllPendingSchools(String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);
        ZonalEducationOffice zonalEducationOffice = zonalEducationOfficeRepo.findById(institutionId).get();
        List<School> pendingSchools = new ArrayList<>();
        for (String schoolIds : zonalEducationOffice.getSchoolsIds()) {
            School school = schoolRepo.findById(schoolIds).get();
            if (SchoolStatus.PENDING.equals(school.getStatus())) {
                pendingSchools.add(school);
            }
        }
        return pendingSchools;
    }

    public List<School> findAllApprovedSchools(String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);
        ZonalEducationOffice zonalEducationOffice = zonalEducationOfficeRepo.findById(institutionId).get();
        List<School> approvedSchools = new ArrayList<>();
        for (String schoolIds : zonalEducationOffice.getSchoolsIds()) {
            School school = schoolRepo.findById(schoolIds).get();
            if (SchoolStatus.APPROVED.equals(school.getStatus())) {
                approvedSchools.add(school);
            }
        }
        return approvedSchools;
    }

    public String generateSchoolNumber() {
        long count = schoolRepo.count(); // total registered schools
        long nextNumber = count + 1;
        return String.format("SCH-%05d", nextNumber);
    }

}
