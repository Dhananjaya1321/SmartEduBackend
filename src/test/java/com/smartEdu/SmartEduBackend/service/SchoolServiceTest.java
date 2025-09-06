package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.enums.*;
import com.smartEdu.SmartEduBackend.repo.*;
import com.smartEdu.SmartEduBackend.service.SchoolService;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchoolServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private SchoolRepo schoolRepo;

    @Mock
    private NationalLevelExamsResultsRepo nationalLevelExamsResultsRepo;

    @Mock
    private ExamRepo examRepo;

    @Mock
    private ALAdmissionRepo alAdmissionRepo;

    @Mock
    private AchievementRepo achievementRepo;

    @Mock
    private ParentRepo parentRepo;

    @Mock
    private StudentRepo studentRepo;

    @Mock
    private GradesRepo gradesRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private PrincipalRepo principalRepo;

    @Mock
    private ZonalEducationOfficeRepo zonalEducationOfficeRepo;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private SchoolService schoolService;

    private SchoolRequest schoolRequest;
    private School school;
    private Principal principal;
    private User user;
    private ZonalEducationOffice zonalOffice;
    private Parent parent;
    private Student student;
    private ALAdmissionRequest alAdmissionRequest;
    private NationalLevelExamsResults examResults;
    private Exam exam;
    private Achievements achievement;
    private ALAdmission alAdmission;

    @BeforeEach
    void setUp() {
        schoolRequest = new SchoolRequest(
                "John Doe", "123456789V", "1234567890", "johndoe", "password", "john@example.com", "123 Main St",
                "nic_front.jpg", "nic_back.jpg", "moe_front.jpg", "moe_back.jpg", "appointment.jpg",
                "Test School", "logo.jpg", "Western", "Colombo", "Colombo North",
                "National", "Type 1", "1-13", "Mixed", "Multi", "English", "1000", "50", 30
        );

        school = School.builder()
                .id("SCH001")
                .schoolNumber("SCH-00001")
                .schoolName("Test School")
                .logoUrl("logo.jpg")
                .province("Western")
                .district("Colombo")
                .zonal("Colombo North")
                .levelOfSchool("National")
                .typeOfSchool("Type 1")
                .gradeSpan("1-13")
                .gender("Mixed")
                .ethnicity("Multi")
                .languageMedium("English")
                .studentPopulation("1000")
                .teacherPopulation("50")
                .classCount(30)
                .status(SchoolStatus.PENDING)
                .build();

        principal = Principal.builder()
                .id("PRIN001")
                .schoolId("SCH001")
                .fullName("John Doe")
                .nicFrontImageUrl("nic_front.jpg")
                .nicBackImageUrl("nic_back.jpg")
                .moeIdFrontImageUrl("moe_front.jpg")
                .moeIdBackImageUrl("moe_back.jpg")
                .appointmentLetterUrl("appointment.jpg")
                .build();

        user = User.builder()
                .id("USER001")
                .nic("123456789V")
                .contact("1234567890")
                .username("johndoe")
                .password("encoded_password")
                .address("123 Main St")
                .email("john@example.com")
                .role(Role.SCHOOL_ADMIN)
                .name("John Doe")
                .active(true)
                .profileId("PRIN001")
                .institutionID("SCH001")
                .build();

        zonalOffice = ZonalEducationOffice.builder()
                .id("ZEO001")
                .schoolsIds(new ArrayList<>(List.of("SCH001")))
                .zonal("Colombo North")
                .district("Colombo")
                .province("Western")
                .build();

        parent = Parent.builder()
                .id("PAR001")
                .studentIds(List.of("STU001"))
                .build();

        student = Student.builder()
                .id("STU001")
                .schoolId("SCH001")
                .fullNameWithInitials("J. Doe")
                .build();

        alAdmissionRequest = ALAdmissionRequest.builder()
                .indexNumber("INDEX123")
                .year("2023")
                .subjectStream("Science")
                .schoolIds(List.of("SCH001"))
                .build();

        examResults = NationalLevelExamsResults.builder()
                .studentId("STU001")
                .indexNumber("INDEX123")
                .examName("G.C.E. (O/L) Examination")
                .year("2023")
                .results(List.of(NationalLevelExamsResult.builder().result("A").build()))
                .build();

        exam = Exam.builder()
                .level(ExamLevel.NATIONAL)
                .examName("G.C.E. (O/L) Examination")
                .year("2023")
                .build();

        achievement = Achievements.builder()
                .studentId("STU001")
                .level(AchievementsLevels.NATIONAL_LEVEL)
                .date(LocalDate.of(2023, 1, 1))
                .build();

        alAdmission = ALAdmission.builder()
                .id("AL001")
                .studentId("STU001")
                .indexNumber("INDEX123")
                .year("2023")
                .subjectStream("Science")
                .schoolId("SCH001")
                .schoolName("Test School")
                .status(ALAdmissionStatus.PENDING)
                .olResults(List.of("A"))
                .olResultsScore(6)
                .residenceScore(4)
                .nationalLevelAchievementsScore(6)
                .provincialLevelAchievementsScore(0)
                .zonalLevelAchievementsScore(0)
                .totalScore(16)
                .build();
    }

    @Test
    void testSaveSchoolWithPrincipal_Success() {
        when(userRepo.findByUsername(schoolRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepo.findByEmail(schoolRequest.getEmail())).thenReturn(Optional.empty());
        when(schoolRepo.count()).thenReturn(0L);
        when(schoolRepo.save(any(School.class))).thenReturn(school);
        when(principalRepo.save(any(Principal.class))).thenReturn(principal);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(zonalEducationOfficeRepo.findByZonalAndDistrictAndProvince(anyString(), anyString(), anyString())).thenReturn(zonalOffice);
        when(zonalEducationOfficeRepo.save(any(ZonalEducationOffice.class))).thenReturn(zonalOffice);

        School result = schoolService.saveSchoolWithPrincipal(schoolRequest);

        assertNotNull(result);
        assertEquals("SCH001", result.getId());
        verify(schoolRepo, times(2)).save(any(School.class));
        verify(principalRepo, times(1)).save(any(Principal.class));
        verify(userRepo, times(1)).save(any(User.class));
        verify(zonalEducationOfficeRepo, times(1)).save(any(ZonalEducationOffice.class));
    }

    @Test
    void testSaveSchoolWithPrincipal_UsernameExists() {
        when(userRepo.findByUsername(schoolRequest.getUsername())).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            schoolService.saveSchoolWithPrincipal(schoolRequest);
        });

        assertEquals("Username is already exists!", exception.getMessage());
        verify(schoolRepo, never()).save(any(School.class));
    }

    @Test
    void testSaveSchoolWithPrincipal_EmailExists() {
        when(userRepo.findByUsername(schoolRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepo.findByEmail(schoolRequest.getEmail())).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            schoolService.saveSchoolWithPrincipal(schoolRequest);
        });

        assertEquals("Email is already exists!", exception.getMessage());
        verify(schoolRepo, never()).save(any(School.class));
    }

    @Test
    void testUpdate_Success() {
        School updatedSchool = School.builder()
                .id("SCH001")
                .schoolName("Updated School")
                .status(SchoolStatus.PENDING)
                .build();

        when(schoolRepo.findById("SCH001")).thenReturn(Optional.of(school));
        when(principalRepo.findBySchoolId("SCH001")).thenReturn(Optional.of(principal));
        when(schoolRepo.save(any(School.class))).thenReturn(updatedSchool);

        School result = schoolService.update("SCH001", updatedSchool);

        assertNotNull(result);
        assertEquals("Updated School", result.getSchoolName());
        verify(schoolRepo, times(1)).save(any(School.class));
    }

    @Test
    void testUpdate_SchoolNotFound() {
        when(schoolRepo.findById("SCH001")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            schoolService.update("SCH001", school);
        });

        assertEquals("School not found!", exception.getMessage());
        verify(schoolRepo, never()).save(any(School.class));
    }

    @Test
    void testUpdateSchoolStatus_Success() {
        when(schoolRepo.findById("SCH001")).thenReturn(Optional.of(school));
        when(schoolRepo.save(any(School.class))).thenReturn(school);

        School result = schoolService.updateSchoolStatus("SCH001", SchoolStatus.APPROVED);

        assertNotNull(result);
        assertEquals(SchoolStatus.APPROVED, result.getStatus());
        verify(schoolRepo, times(1)).save(any(School.class));
    }

    @Test
    void testDelete_Success() {
        when(schoolRepo.findById("SCH001")).thenReturn(Optional.of(school));
        when(jwtUtil.extractInstitutionId("token")).thenReturn("ZEO001");
        when(zonalEducationOfficeRepo.findById("ZEO001")).thenReturn(Optional.of(zonalOffice));
        when(zonalEducationOfficeRepo.save(any(ZonalEducationOffice.class))).thenReturn(zonalOffice);

        schoolService.delete("SCH001", "token");

        verify(zonalEducationOfficeRepo, times(1)).save(any(ZonalEducationOffice.class));
        verify(schoolRepo, times(1)).deleteById("SCH001");
    }

    @Test
    void testDelete_SchoolNotFound() {
        when(schoolRepo.findById("SCH001")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            schoolService.delete("SCH001", "token");
        });

        assertEquals("School not found!", exception.getMessage());
        verify(schoolRepo, never()).deleteById(anyString());
    }

    @Test
    void testFindById_Success() {
        when(schoolRepo.findById("SCH001")).thenReturn(Optional.of(school));

        Optional<School> result = schoolService.findById("SCH001");

        assertTrue(result.isPresent());
        assertEquals("Test School", result.get().getSchoolName());
    }

    @Test
    void testFindAllPendingSchools_Success() {
        when(jwtUtil.extractInstitutionId("token")).thenReturn("ZEO001");
        when(zonalEducationOfficeRepo.findById("ZEO001")).thenReturn(Optional.of(zonalOffice));
        when(schoolRepo.findById("SCH001")).thenReturn(Optional.of(school));

        List<School> result = schoolService.findAllPendingSchools("token");

        assertEquals(1, result.size());
        assertEquals("Test School", result.get(0).getSchoolName());
    }

    @Test
    void testFindAllApprovedSchools_Success() {
        school.setStatus(SchoolStatus.APPROVED);
        when(jwtUtil.extractInstitutionId("token")).thenReturn("ZEO001");
        when(zonalEducationOfficeRepo.findById("ZEO001")).thenReturn(Optional.of(zonalOffice));
        when(schoolRepo.findById("SCH001")).thenReturn(Optional.of(school));

        List<School> result = schoolService.findAllApprovedSchools("token");

        assertEquals(1, result.size());
        assertEquals("Test School", result.get(0).getSchoolName());
    }

    @Test
    void testGenerateSchoolNumber_Success() {
        when(schoolRepo.count()).thenReturn(1L);

        String schoolNumber = schoolService.generateSchoolNumber();

        assertEquals("SCH-00002", schoolNumber);
    }

    @Test
    void testGetAllSchoolsByProvinceAndDistrictAndZonal_Success() {
      /*  when(schoolRepo.findByProvinceAndDistrictAndZonal("Western", "Colombo", "Colombo North")).thenReturn(Optional.of(school));

        List<School> result = schoolService.getAllSchoolsByProvinceAndDistrictAndZonal("Western", "Colombo", "Colombo North");

        assertTrue(result.isPresent());
        assertEquals("Test School", result.get().getSchoolName());*/
    }

    @Test
    void testSearchAllSchoolsToParentsCanApplyForALs_Success() {
        when(jwtUtil.extractUsername("token")).thenReturn("johndoe");
        when(userRepo.findByUsername("johndoe")).thenReturn(Optional.of(user));
        when(parentRepo.findById("PRIN001")).thenReturn(Optional.of(parent));
        when(studentRepo.findById("STU001")).thenReturn(Optional.of(student));
        when(schoolRepo.findById("SCH001")).thenReturn(Optional.of(school));
        when(schoolRepo.findBySchoolNameAndProvince("Test School", "Western")).thenReturn(List.of(school));

        List<School> result = schoolService.searchAllSchoolsToParentsCanApplyForALs("Test School", "token");

        assertEquals(1, result.size());
        assertEquals("Test School", result.get(0).getSchoolName());
    }

    @Test
    void testGetAllSchoolsToParentsCanApplyForALs_Success() {
        when(jwtUtil.extractUsername("token")).thenReturn("johndoe");
        when(userRepo.findByUsername("johndoe")).thenReturn(Optional.of(user));
        when(parentRepo.findById("PRIN001")).thenReturn(Optional.of(parent));
        when(studentRepo.findById("STU001")).thenReturn(Optional.of(student));
        when(schoolRepo.findById("SCH001")).thenReturn(Optional.of(school));
        when(schoolRepo.findByProvince("Western")).thenReturn(List.of(school));

        List<School> result = schoolService.getAllSchoolsToParentsCanApplyForALs("token");

        assertEquals(1, result.size());
        assertEquals("Test School", result.get(0).getSchoolName());
    }

    @Test
    void testApplySchoolsToParentsForALs_Success() {
        when(jwtUtil.extractUsername("token")).thenReturn("johndoe");
        when(userRepo.findByUsername("johndoe")).thenReturn(Optional.of(user));
        when(parentRepo.findById("PRIN001")).thenReturn(Optional.of(parent));
        when(studentRepo.findById("STU001")).thenReturn(Optional.of(student));
        when(schoolRepo.findById("SCH001")).thenReturn(Optional.of(school));
        when(nationalLevelExamsResultsRepo.findByIndexNumberAndExamNameAndYear("INDEX123", "G.C.E. (O/L) Examination", "2023")).thenReturn(examResults);
        when(examRepo.findByLevelAndExamNameAndYear(ExamLevel.NATIONAL, "G.C.E. (O/L) Examination", "2023")).thenReturn(exam);
        when(achievementRepo.findByStudentIdAndDateBetween(eq("STU001"), any(LocalDate.class), any(LocalDate.class))).thenReturn(List.of(achievement));
        when(alAdmissionRepo.save(any(ALAdmission.class))).thenReturn(alAdmission);

        ALAdmissionRequest result = schoolService.applySchoolsToParentsForALs(alAdmissionRequest, "token");

        assertNotNull(result);
        assertEquals("INDEX123", result.getIndexNumber());
        verify(alAdmissionRepo, times(1)).save(any(ALAdmission.class));
    }

    @Test
    void testApplySchoolsToParentsForALs_InvalidIndexNumber() {
        examResults.setStudentId("STU002");
        when(jwtUtil.extractUsername("token")).thenReturn("johndoe");
        when(userRepo.findByUsername("johndoe")).thenReturn(Optional.of(user));
        when(parentRepo.findById("PRIN001")).thenReturn(Optional.of(parent));
        when(studentRepo.findById("STU001")).thenReturn(Optional.of(student));
        when(schoolRepo.findById("SCH001")).thenReturn(Optional.of(school));
        when(nationalLevelExamsResultsRepo.findByIndexNumberAndExamNameAndYear("INDEX123", "G.C.E. (O/L) Examination", "2023")).thenReturn(examResults);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            schoolService.applySchoolsToParentsForALs(alAdmissionRequest, "token");
        });

        assertEquals("The index number don't match with your index number!", exception.getMessage());
        verify(alAdmissionRepo, never()).save(any(ALAdmission.class));
    }

    @Test
    void testGetAllALAdmissionsStatusToParents_Success() {
        when(jwtUtil.extractUsername("token")).thenReturn("johndoe");
        when(userRepo.findByUsername("johndoe")).thenReturn(Optional.of(user));
        when(parentRepo.findById("PRIN001")).thenReturn(Optional.of(parent));
        when(studentRepo.findById("STU001")).thenReturn(Optional.of(student));
        when(alAdmissionRepo.findByStudentId("STU001")).thenReturn(List.of(alAdmission));

        List<ALAdmission> result = schoolService.getAllALAdmissionsStatusToParents("token");

        assertEquals(1, result.size());
        assertEquals("STU001", result.get(0).getStudentId());
    }

    @Test
    void testGetAllALAdmissionsToSchools_Success() {
        when(jwtUtil.extractInstitutionId("token")).thenReturn("SCH001");
        when(alAdmissionRepo.findBySchoolId("SCH001")).thenReturn(List.of(alAdmission));
        when(studentRepo.findById("STU001")).thenReturn(Optional.of(student));

        List<ALAdmission> result = schoolService.getAllALAdmissionsToSchools("token");

        assertEquals(1, result.size());
        assertEquals("J. Doe", result.get(0).getStudentName());
    }

    @Test
    void testGetAllALAdmissionsAcceptedByStudentToSchools_Success() {
        alAdmission.setStatus(ALAdmissionStatus.STUDENT_ACCEPTED);
        when(jwtUtil.extractInstitutionId("token")).thenReturn("SCH001");
        when(alAdmissionRepo.findBySchoolId("SCH001")).thenReturn(List.of(alAdmission));
        when(studentRepo.findById("STU001")).thenReturn(Optional.of(student));

        List<ALAdmission> result = schoolService.getAllALAdmissionsAcceptedByStudentToSchools("token");

        assertEquals(1, result.size());
        assertEquals("J. Doe", result.get(0).getStudentName());
    }

    @Test
    void testAcceptTheALApplication_Success() {
        when(alAdmissionRepo.findById("AL001")).thenReturn(Optional.of(alAdmission));
        when(alAdmissionRepo.save(any(ALAdmission.class))).thenReturn(alAdmission);

        ALAdmission result = schoolService.acceptTheALApplication("AL001");

        assertNotNull(result);
        assertEquals(ALAdmissionStatus.SCHOOL_ACCEPTED, result.getStatus());
        verify(alAdmissionRepo, times(1)).save(any(ALAdmission.class));
    }

    @Test
    void testAcceptTheALApplicationStudent_Success() {
        when(alAdmissionRepo.findById("AL001")).thenReturn(Optional.of(alAdmission));
        when(alAdmissionRepo.findByStudentId("STU001")).thenReturn(List.of(alAdmission));
        when(alAdmissionRepo.save(any(ALAdmission.class))).thenReturn(alAdmission);

        ALAdmission result = schoolService.acceptTheALApplicationStudent("AL001");

        assertNotNull(result);
        assertEquals(ALAdmissionStatus.STUDENT_ACCEPTED, result.getStatus());
        verify(alAdmissionRepo, times(1)).save(any(ALAdmission.class));
    }

    @Test
    void testAcceptTheALApplicationStudent_AlreadyAccepted() {
        alAdmission.setStatus(ALAdmissionStatus.STUDENT_ACCEPTED);
        when(alAdmissionRepo.findById("AL001")).thenReturn(Optional.of(alAdmission));
        when(alAdmissionRepo.findByStudentId("STU001")).thenReturn(List.of(alAdmission));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            schoolService.acceptTheALApplicationStudent("AL001");
        });

        assertEquals("You already accept the school", exception.getMessage());
        verify(alAdmissionRepo, never()).save(any(ALAdmission.class));
    }

    @Test
    void testRejectTheALApplicationStudent_Success() {
        when(alAdmissionRepo.findById("AL001")).thenReturn(Optional.of(alAdmission));
        when(alAdmissionRepo.save(any(ALAdmission.class))).thenReturn(alAdmission);

        ALAdmission result = schoolService.rejectTheALApplicationStudent("AL001");

        assertNotNull(result);
        assertEquals(ALAdmissionStatus.STUDENT_REJECTED, result.getStatus());
        verify(alAdmissionRepo, times(1)).save(any(ALAdmission.class));
    }
}
