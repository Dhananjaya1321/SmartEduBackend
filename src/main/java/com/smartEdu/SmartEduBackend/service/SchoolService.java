package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.Principal;
import com.smartEdu.SmartEduBackend.entity.School;
import com.smartEdu.SmartEduBackend.entity.SchoolRequest;

import com.smartEdu.SmartEduBackend.repo.PrincipalRepo;
import com.smartEdu.SmartEduBackend.repo.SchoolRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SchoolService {

    @Autowired
    private SchoolRepo schoolRepo;

    @Autowired
    private PrincipalRepo principalRepo;

    // Save a new school with principal
    public School saveWithPrincipal(SchoolRequest request) {
        Principal principal = request.getPrincipal();
        Principal savedPrincipal = principalRepo.save(principal);

        School school = request.getSchool();
        school.setSchoolNumber(generateSchoolNumber());
        school.setPrincipal(savedPrincipal);

        School savedSchool = schoolRepo.save(school);
        savedPrincipal.setSchoolId(savedSchool.getId());
        principalRepo.save(savedPrincipal);

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
