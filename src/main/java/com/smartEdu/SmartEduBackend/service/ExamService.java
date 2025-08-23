package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.enums.ExamLevel;
import com.smartEdu.SmartEduBackend.enums.Role;
import com.smartEdu.SmartEduBackend.repo.*;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExamService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private GradesRepo gradesRepo;

    @Autowired
    private ParentRepo parentRepo;

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

        String str = exam.getGrade();
        exam.setGrade(str.split("_")[1]);
        return examRepo.save(exam);
    }

    public Exam update(String id, Exam exam) throws Exception {
        Optional<Exam> existing = examRepo.findById(id);
        if (existing.isPresent()) {
            exam.setId(id);
            exam.setInstitutionId(existing.get().getInstitutionId());
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

    public List<Exam> findByGrade(String grade, String token) {
        return examRepo.findByGrade(grade);
    }

    public List<Exam> getByGradeTermExamsToParents(String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username).get();
        String profileId = user.getProfileId();

        Parent parent = parentRepo.findById(profileId).get();
        Student student = studentRepo.findById(parent.getStudentIds().getFirst()).get();
        String gradeId = student.getGradeId();
        Grades grades = gradesRepo.findById(gradeId).get();

        String year = String.valueOf(LocalDate.now().getYear());

        ZonalEducationOffice zonalEducationOffice = zonalEducationOfficeRepo.findAllBySchoolsIds(student.getSchoolId());
        ProvincialEducationOffice provincialEducationOffice = provincialEducationOfficeRepo.findByProvince(zonalEducationOffice.getProvince());

        List<Exam> exams = new ArrayList<>();

        List<Exam> schoolLevel = examRepo.findByGradeAndInstitutionIdAndYear(grades.getGradeName(), student.getSchoolId(), year);
        List<Exam> zonalLevel = examRepo.findByGradeAndInstitutionIdAndYear(grades.getGradeName(), zonalEducationOffice.getId(), year);
        List<Exam> provincialLevel = examRepo.findByGradeAndInstitutionIdAndYear(grades.getGradeName(), provincialEducationOffice.getId(), year);
        List<Exam> nationalLevel = examRepo.findByGradeAndLevelAndYear(grades.getGradeName(), ExamLevel.NATIONAL, year);

        exams.addAll(schoolLevel);
        exams.addAll(zonalLevel);
        exams.addAll(provincialLevel);
        exams.addAll(nationalLevel);

        List<Exam> termExams = new ArrayList<>();
        for (Exam e : exams) {
            if (e.getExamName().equals("First Term Exam") || e.getExamName().equals("Mid-Term Exam") || e.getExamName().equals("Final Term Exam")) {
                termExams.add(e);
            }
        }


        return termExams;
    }

    public List<Exam> getByGradeTermExamsToTeacher(String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);
        String year = String.valueOf(LocalDate.now().getYear());

        ZonalEducationOffice zonalEducationOffice = zonalEducationOfficeRepo.findAllBySchoolsIds(institutionId);
        ProvincialEducationOffice provincialEducationOffice = provincialEducationOfficeRepo.findByProvince(zonalEducationOffice.getProvince());

        List<Exam> exams = new ArrayList<>();

        List<Exam> schoolLevel = examRepo.findByInstitutionIdAndYear(institutionId, year);
        List<Exam> zonalLevel = examRepo.findByInstitutionIdAndYear(zonalEducationOffice.getId(), year);
        List<Exam> provincialLevel = examRepo.findByInstitutionIdAndYear(provincialEducationOffice.getId(), year);
        List<Exam> nationalLevel = examRepo.findByLevelAndYear(ExamLevel.NATIONAL, year);

        exams.addAll(schoolLevel);
        exams.addAll(zonalLevel);
        exams.addAll(provincialLevel);
        exams.addAll(nationalLevel);

        List<Exam> termExams = new ArrayList<>();
        for (Exam e : exams) {
            if (e.getExamName().equals("First Term Exam") || e.getExamName().equals("Mid-Term Exam") || e.getExamName().equals("Final Term Exam")) {
                termExams.add(e);
            }
        }


        return termExams;
    }

    public List<Exam> getByGradeALExamsToParents() {
        String year = String.valueOf(LocalDate.now().getYear());
        return examRepo.findByGradeAndLevelAndYearAndExamName("13", ExamLevel.NATIONAL, year,"G.C.E. (A/L) Examination");
    }

    public List<Exam> getByGradeOLExamsToParents() {
        String year = String.valueOf(LocalDate.now().getYear());
        return examRepo.findByGradeAndLevelAndYearAndExamName("11", ExamLevel.NATIONAL, year,"G.C.E. (O/L) Examination");
    }

    public List<Exam> getByGradeG5ExamsToParents() {
        String year = String.valueOf(LocalDate.now().getYear());
        return examRepo.findByGradeAndLevelAndYearAndExamName("5", ExamLevel.NATIONAL, year,"Grade 5 Scholarship Examination");
    }

    public List<Exam> findByYear(String year) {
        return examRepo.findByYear(year);
    }


}
