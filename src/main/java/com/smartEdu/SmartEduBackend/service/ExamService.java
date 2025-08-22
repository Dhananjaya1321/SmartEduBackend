package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.Exam;
import com.smartEdu.SmartEduBackend.entity.ProvincialEducationOffice;
import com.smartEdu.SmartEduBackend.entity.User;
import com.smartEdu.SmartEduBackend.entity.ZonalEducationOffice;
import com.smartEdu.SmartEduBackend.enums.ExamLevel;
import com.smartEdu.SmartEduBackend.enums.Role;
import com.smartEdu.SmartEduBackend.repo.ExamRepo;
import com.smartEdu.SmartEduBackend.repo.ProvincialEducationOfficeRepo;
import com.smartEdu.SmartEduBackend.repo.UserRepo;
import com.smartEdu.SmartEduBackend.repo.ZonalEducationOfficeRepo;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExamService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ExamRepo examRepo;

    @Autowired
    private ZonalEducationOfficeRepo zonalEducationOfficeRepo;

    @Autowired
    private ProvincialEducationOfficeRepo provincialEducationOfficeRepo;

    @Autowired
    private UserRepo userRepo;

    public Exam save(Exam exam, String token) {
        String username = jwtUtil.extractUsername(token);
        String institutionId = jwtUtil.extractInstitutionId(token);
        User user = userRepo.findByUsername(username).get();
        Role role = user.getRole();
        ExamLevel examLevel = null;
        switch (role) {
            case MOE_ADMIN -> {
                examLevel = ExamLevel.NATIONAL;
            }
            case MOE_EMPLOYEE -> {
                examLevel = ExamLevel.NATIONAL;
            }
            case PMOE_ADMIN -> {
                examLevel = ExamLevel.PROVINCE;
            }
            case PMOE_EMPLOYEE -> {
                examLevel = ExamLevel.PROVINCE;
            }
            case ZMOE_ADMIN -> {
                examLevel = ExamLevel.ZONAL;
            }
            case ZMOE_EMPLOYEE -> {
                examLevel = ExamLevel.ZONAL;
            }
            case SCHOOL_ADMIN -> {
                examLevel = ExamLevel.SCHOOL;
            }
            case SCHOOL_EMPLOYEE -> {
                examLevel = ExamLevel.SCHOOL;
            }
        }
        exam.setLevel(examLevel);
        exam.setInstitutionId(institutionId);
        return examRepo.save(exam);
    }

    public Exam update(String id, Exam exam) throws Exception {
        Optional<Exam> existing = examRepo.findById(id);
        if (existing.isPresent()) {
            exam.setId(id);
            return examRepo.save(exam);
        } else {
            throw new Exception("Exam not found!");
        }
    }

    public void delete(String id) throws Exception {
        if (!examRepo.existsById(id)) {
            throw new Exception("Exam not found!");
        }
        examRepo.deleteById(id);
    }

    public Optional<Exam> findById(String id) {
        return examRepo.findById(id);
    }

    public List<Exam> findAll(String token) {
        String username = jwtUtil.extractUsername(token);
        String institutionId = jwtUtil.extractInstitutionId(token);
        User user = userRepo.findByUsername(username).get();
        Role role = user.getRole();
        List<Exam> exams = new ArrayList<>();

        if (role.equals(Role.SCHOOL_ADMIN) || role.equals(Role.SCHOOL_EMPLOYEE)) {
            ZonalEducationOffice zonalEducationOffice = zonalEducationOfficeRepo.findAllBySchoolsIds(institutionId);
            ProvincialEducationOffice provincialEducationOffice = provincialEducationOfficeRepo.findByProvince(zonalEducationOffice.getProvince());

            List<Exam> schoolLevel = examRepo.findByInstitutionId(institutionId);
            List<Exam> zonalLevel = examRepo.findByInstitutionId(zonalEducationOffice.getId());
            List<Exam> provincialLevel = examRepo.findByInstitutionId(provincialEducationOffice.getId());
            List<Exam> nationalLevel = examRepo.findByLevel(ExamLevel.NATIONAL);

            exams.addAll(schoolLevel);
            exams.addAll(zonalLevel);
            exams.addAll(provincialLevel);
            exams.addAll(nationalLevel);
        } else if (role.equals(Role.ZMOE_ADMIN) || role.equals(Role.ZMOE_EMPLOYEE)) {
            ZonalEducationOffice zonalEducationOffice = zonalEducationOfficeRepo.findById(institutionId).get();
            List<Exam> zonalLevel = examRepo.findByInstitutionId(zonalEducationOffice.getId());
            exams.addAll(zonalLevel);
        } else if (role.equals(Role.PMOE_ADMIN) || role.equals(Role.PMOE_EMPLOYEE)) {
            ProvincialEducationOffice provincialEducationOffice = provincialEducationOfficeRepo.findById(institutionId).get();
            List<Exam> provincialLevel = examRepo.findByInstitutionId(provincialEducationOffice.getId());
            exams.addAll(provincialLevel);
        } else {
            List<Exam> nationalLevel = examRepo.findByLevel(ExamLevel.NATIONAL);
            exams.addAll(nationalLevel);
        }
        return exams;
    }

    public List<Exam> findByGrade(String grade) {
        return examRepo.findByGrade(grade);
    }

    public List<Exam> findByYear(int year) {
        return examRepo.findByYear(year);
    }


}
