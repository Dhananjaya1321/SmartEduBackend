package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;

import com.smartEdu.SmartEduBackend.enums.*;
import com.smartEdu.SmartEduBackend.repo.*;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private NationalLevelExamsResultsRepo nationalLevelExamsResultsRepo;

    @Autowired
    private ExamRepo examRepo;

    @Autowired
    private ALAdmissionRepo alAdmissionRepo;

    @Autowired
    private AchievementRepo achievementRepo;

    @Autowired
    private ParentRepo parentRepo;

    @Autowired
    private StudentRepo studentRepo;

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
    public void delete(String id, String token) {
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

    public Optional<School> getAllSchoolsByProvinceAndDistrictAndZonal(String province, String district, String zonal) {
        return schoolRepo.findByProvinceAndDistrictAndZonal(province, district, zonal);
    }

    public List<School> searchAllSchoolsToParentsCanApplyForALs(String schoolName, String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username).get();
        String profileId = user.getProfileId();

        Parent parent = parentRepo.findById(profileId).get();
        Student student = studentRepo.findById(parent.getStudentIds().getFirst()).get();
        School school = schoolRepo.findById(student.getSchoolId()).get();
        String province = school.getProvince();

        return schoolRepo.findBySchoolNameAndProvince(schoolName, province);
    }

    public List<School> getAllSchoolsToParentsCanApplyForALs(String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username).get();
        String profileId = user.getProfileId();

        Parent parent = parentRepo.findById(profileId).get();
        Student student = studentRepo.findById(parent.getStudentIds().getFirst()).get();
        School school = schoolRepo.findById(student.getSchoolId()).get();
        String province = school.getProvince();

        return schoolRepo.findByProvince(province);
    }

    public ALAdmissionRequest applySchoolsToParentsForALs(ALAdmissionRequest request, String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username).get();
        String profileId = user.getProfileId();

        Parent parent = parentRepo.findById(profileId).get();
        Student student = studentRepo.findById(parent.getStudentIds().getFirst()).get();
        School school = schoolRepo.findById(student.getSchoolId()).get();

        List<String> olResults = new ArrayList<>();

        NationalLevelExamsResults byIndexNumberAndExamNameAndYear = nationalLevelExamsResultsRepo.findByIndexNumberAndExamNameAndYear(request.getIndexNumber(), "G.C.E. (O/L) Examination", request.getYear());
        if (!byIndexNumberAndExamNameAndYear.getStudentId().equals(student.getId()))
            throw new RuntimeException("The index number don't match with your index number!");

        for (NationalLevelExamsResult ol : byIndexNumberAndExamNameAndYear.getResults()) {
            olResults.add(ol.getResult());
        }

        ALAdmission alAdmission = ALAdmission.builder()
                .studentId(student.getId())
                .indexNumber(request.getIndexNumber())
                .year(request.getYear())
                .subjectStream(request.getSubjectStream())
                .status(ALAdmissionStatus.PENDING)
                .olResults(olResults)
                .build();

        int olResultsScore = 0;
        int nationalLevelAchievementsScore = 0;
        int provincialLevelAchievementsScore = 0;
        int zonalLevelAchievementsScore = 0;
        int totalScore = 0;

        for (String result : olResults) {
            if (result.equals("A")) {
                olResultsScore += 6;
            } else if (result.equals("B")) {
                olResultsScore += 5;
            } else if (result.equals("C")) {
                olResultsScore += 4;
            } else if (result.equals("S")) {
                olResultsScore += 2;
            } else {
                olResultsScore += 0;
            }
        }
        alAdmission.setOlResultsScore(olResultsScore);

        Exam olExam = examRepo.findByLevelAndExamNameAndYear(ExamLevel.NATIONAL, "G.C.E. (O/L) Examination", request.getYear());
        int endYear = Integer.parseInt(olExam.getYear());
        int startYear = endYear - 2;
        LocalDate startDate = LocalDate.of(startYear, 1, 1);
        LocalDate endDate = LocalDate.of(endYear, 1, 1);

        List<Achievements> achievements = achievementRepo.findByStudentIdAndDateBetween(student.getId(), startDate, endDate);
        for (Achievements a : achievements) {
            if (a.getLevel().equals(AchievementsLevels.NATIONAL_LEVEL)) {
                nationalLevelAchievementsScore += 6;
            } else if (a.getLevel().equals(AchievementsLevels.PROVINCIAL_LEVEL)) {
                provincialLevelAchievementsScore += 5;
            } else if (a.getLevel().equals(AchievementsLevels.ZONAL_LEVEL)) {
                zonalLevelAchievementsScore += 4;
            }
        }
        alAdmission.setNationalLevelAchievementsScore(nationalLevelAchievementsScore);
        alAdmission.setProvincialLevelAchievementsScore(provincialLevelAchievementsScore);
        alAdmission.setZonalLevelAchievementsScore(zonalLevelAchievementsScore);

        for (String sclId : request.getSchoolIds()) {
            int residenceScore = 0;
            School selectedSchool = schoolRepo.findById(sclId).get();
            if (selectedSchool.getZonal().equals(school.getZonal())) {
                residenceScore = 4;
            } else if (selectedSchool.getDistrict().equals(school.getDistrict())) {
                residenceScore = 3;
            } else if (selectedSchool.getProvince().equals(school.getProvince())) {
                residenceScore = 2;
            }
            alAdmission.setResidenceScore(residenceScore);
            totalScore = olResultsScore + residenceScore + nationalLevelAchievementsScore + provincialLevelAchievementsScore + zonalLevelAchievementsScore;
            alAdmission.setTotalScore(totalScore);
            alAdmission.setSchoolId(sclId);
            alAdmission.setSchoolName(selectedSchool.getSchoolName());

            alAdmissionRepo.save(alAdmission);
        }
        return request;
    }

    public List<ALAdmission> getAllALAdmissionsStatusToParents(String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username).get();
        String profileId = user.getProfileId();

        Parent parent = parentRepo.findById(profileId).get();
        Student student = studentRepo.findById(parent.getStudentIds().getFirst()).get();
        return alAdmissionRepo.findByStudentId(student.getId());
    }

    public List<ALAdmission> getAllALAdmissionsToSchools(String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);
        List<ALAdmission> alAdmissions = new ArrayList<>();
        List<ALAdmission> bySchoolId = alAdmissionRepo.findBySchoolId(institutionId);
        for (ALAdmission a : bySchoolId) {
            if (!a.getStatus().equals(ALAdmissionStatus.STUDENT_REJECTED) &&
                    !a.getStatus().equals(ALAdmissionStatus.SCHOOL_ACCEPTED) &&
                    !a.getStatus().equals(ALAdmissionStatus.STUDENT_ACCEPTED)) {
                String fullNameWithInitials = studentRepo.findById(a.getStudentId()).get().getFullNameWithInitials();
                a.setStudentName(fullNameWithInitials);
                alAdmissions.add(a);
            }
        }
        return alAdmissions;
    }

    public ALAdmission acceptTheALApplication(String id) {
        ALAdmission alAdmission = alAdmissionRepo.findById(id).get();
        alAdmission.setStatus(ALAdmissionStatus.SCHOOL_ACCEPTED);
        return alAdmissionRepo.save(alAdmission);
    }
}
