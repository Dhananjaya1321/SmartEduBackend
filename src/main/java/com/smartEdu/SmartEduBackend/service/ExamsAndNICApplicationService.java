package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.enums.ExamsAndNICApplicationStatus;
import com.smartEdu.SmartEduBackend.repo.ExamsAndNICApplicationRepo;
import com.smartEdu.SmartEduBackend.repo.ParentRepo;
import com.smartEdu.SmartEduBackend.repo.StudentRepo;
import com.smartEdu.SmartEduBackend.repo.UserRepo;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ExamsAndNICApplicationService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ParentRepo parentRepo;

    @Autowired
    private ExamsAndNICApplicationRepo examsAndNICApplicationRepo;

    public ExamsAndNICApplication save(ExamsAndNICApplication examsAndNICApplication, String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);

        examsAndNICApplication.setStatus(ExamsAndNICApplicationStatus.PENDING);
        examsAndNICApplication.setSchoolId(institutionId);
        return examsAndNICApplicationRepo.save(examsAndNICApplication);
    }

    public List<ExamsAndNICApplicationResponse> findAll(String applicationType, String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);
        List<ExamsAndNICApplicationResponse> examsAndNICApplicationResponses = new ArrayList<>();
        List<ExamsAndNICApplication> byTypeAndSchoolId = examsAndNICApplicationRepo.findByTypeAndSchoolId(applicationType, institutionId);
        for (ExamsAndNICApplication e : byTypeAndSchoolId) {
            Student student = studentRepo.findById(e.getStudentId()).get();
            examsAndNICApplicationResponses.add(ExamsAndNICApplicationResponse.builder()
                    .id(e.getId())
                    .studentId(e.getStudentId())
                    .studentName(student.getFullNameWithInitials())
                    .registrationNumber(student.getRegistrationNumber())
                    .schoolId(e.getSchoolId())
                    .type(e.getType())
                    .status(e.getStatus())
                    .nicFrontImageUrl(e.getNicFrontImageUrl())
                    .nicBackImageUrl(e.getNicBackImageUrl())
                    .birthCertificateFrontImageUrl(e.getBirthCertificateFrontImageUrl())
                    .birthCertificateBackImageUrl(e.getBirthCertificateBackImageUrl())
                    .build());
        }
        return examsAndNICApplicationResponses;
    }

    public List<ExamsAndNICApplicationResponse> findAllToParents(String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username).get();
        String profileId = user.getProfileId();

        Parent parent = parentRepo.findById(profileId).get();
        Student student = studentRepo.findById(parent.getStudentIds().getFirst()).get();
        return examsAndNICApplicationRepo.findByStudentId(student.getId());
    }
}
