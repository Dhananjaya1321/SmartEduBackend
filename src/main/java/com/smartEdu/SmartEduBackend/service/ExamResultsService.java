package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.repo.*;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExamResultsService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ExamResultsRepo examResultsRepo;

    public ExamResults save(ExamResults exam, String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);
        exam.setSchoolId(institutionId);
        return examResultsRepo.save(exam);
    }
}
