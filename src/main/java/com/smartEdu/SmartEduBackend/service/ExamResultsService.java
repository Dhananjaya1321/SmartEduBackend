package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.repo.*;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ExamResultsService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SubjectResultsRepo subjectResultsRepo;

    public SubjectResults save(SubjectResults exam, String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);
        exam.setSchoolId(institutionId);
        return subjectResultsRepo.save(exam);
    }
}
