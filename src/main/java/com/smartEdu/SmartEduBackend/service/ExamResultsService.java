package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.enums.ExamsResults;
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
    private UserRepo userRepo;

    @Autowired
    private GradesRepo gradesRepo;

    @Autowired
    private ClassRoomRepo classRoomRepo;

    @Autowired
    private ExamResultsRepo examResultsRepo;

    @Autowired
    private SubjectResultsRepo subjectResultsRepo;

    public SubjectResults save(SubjectResults exam, String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);
        exam.setSchoolId(institutionId);
        return subjectResultsRepo.save(exam);
    }

    public ExamResults saveExamResults(ExamResults examResults, String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username).get();
        ClassRoom classRoom = classRoomRepo.findByClassTeacherId(user.getProfileId());
        Grades byGradeNameAndSchoolId = gradesRepo.findByGradeNameAndSchoolId(examResults.getGradeName(), institutionId);


        examResults.setExamResultsStatus(ExamsResults.RELEASED);
        examResults.setClassId(classRoom.getId());
        examResults.setGradeId(byGradeNameAndSchoolId.getId());

        return examResultsRepo.save(examResults);
    }
}
