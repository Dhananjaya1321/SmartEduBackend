package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.enums.ExamLevel;
import com.smartEdu.SmartEduBackend.enums.ExamsResults;
import com.smartEdu.SmartEduBackend.enums.ExamsSubjectResults;
import com.smartEdu.SmartEduBackend.enums.Role;
import com.smartEdu.SmartEduBackend.repo.*;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ExamServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ClassRoomRepo classRoomRepo;

    @Mock
    private StudentRepo studentRepo;

    @Mock
    private ExamResultsRepo examResultsRepo;

    @Mock
    private SubjectResultsRepo subjectResultsRepo;

    @Mock
    private GradesRepo gradesRepo;

    @Mock
    private ParentRepo parentRepo;

    @Mock
    private ExamRepo examRepo;

    @Mock
    private ZonalEducationOfficeRepo zonalEducationOfficeRepo;

    @Mock
    private ProvincialEducationOfficeRepo provincialEducationOfficeRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private ExamService examService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave_SchoolAdmin_SavesExamWithSchoolLevel() {
        String token = "validToken";
        String username = "user1";
        String institutionId = "school123";
        User user = User.builder().username(username).role(Role.SCHOOL_ADMIN).build();
        Exam exam = Exam.builder().examName("First Term Exam").grade("grade_5").build();
        Exam savedExam = Exam.builder().id("exam1").examName("First Term Exam").grade("5").institutionId(institutionId).level(ExamLevel.SCHOOL).build();

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(jwtUtil.extractInstitutionId(token)).thenReturn(institutionId);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(examRepo.save(any(Exam.class))).thenReturn(savedExam);

        Exam result = examService.save(exam, token);

        assertEquals(savedExam, result);
        assertEquals("5", result.getGrade());
        assertEquals(institutionId, result.getInstitutionId());
        assertEquals(ExamLevel.SCHOOL, result.getLevel());
        verify(examRepo, times(1)).save(any(Exam.class));
    }

    @Test
    void testSave_MOEAdmin_SavesExamWithNationalLevel() {
        String token = "validToken";
        String username = "user1";
        String institutionId = "moe123";
        User user = User.builder().username(username).role(Role.MOE_ADMIN).build();
        Exam exam = Exam.builder().examName("G.C.E. (A/L) Examination").grade("grade_13").build();
        Exam savedExam = Exam.builder().id("exam1").examName("G.C.E. (A/L) Examination").grade("13").institutionId(institutionId).level(ExamLevel.NATIONAL).build();

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(jwtUtil.extractInstitutionId(token)).thenReturn(institutionId);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(examRepo.save(any(Exam.class))).thenReturn(savedExam);

        Exam result = examService.save(exam, token);

        assertEquals(savedExam, result);
        assertEquals("13", result.getGrade());
        assertEquals(institutionId, result.getInstitutionId());
        assertEquals(ExamLevel.NATIONAL, result.getLevel());
        verify(examRepo, times(1)).save(any(Exam.class));
    }

    @Test
    void testUpdate_ExistingExam_UpdatesExam() throws Exception {
        String id = "exam1";
        String institutionId = "school123";
        Exam existingExam = Exam.builder().id(id).institutionId(institutionId).build();
        Exam updatedExam = Exam.builder().examName("Updated Exam").grade("grade_5").build();
        Exam savedExam = Exam.builder().id(id).examName("Updated Exam").grade("5").institutionId(institutionId).build();

        when(examRepo.findById(id)).thenReturn(Optional.of(existingExam));
        when(examRepo.save(any(Exam.class))).thenReturn(savedExam);

        Exam result = examService.update(id, updatedExam);

        assertEquals(savedExam, result);
        assertEquals(id, result.getId());
        assertEquals(institutionId, result.getInstitutionId());
        assertEquals("5", result.getGrade());
        verify(examRepo, times(1)).save(any(Exam.class));
    }

    @Test
    void testUpdate_NonExistingExam_ThrowsException() {
        String id = "nonExisting";
        Exam updatedExam = Exam.builder().examName("Updated Exam").build();

        when(examRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> examService.update(id, updatedExam));
        verify(examRepo, never()).save(any(Exam.class));
    }

    @Test
    void testDelete_ExistingExam_DeletesExam() throws Exception {
        String id = "exam1";

        when(examRepo.existsById(id)).thenReturn(true);

        examService.delete(id);

        verify(examRepo, times(1)).deleteById(id);
    }

    @Test
    void testDelete_NonExistingExam_ThrowsException() {
        String id = "nonExisting";

        when(examRepo.existsById(id)).thenReturn(false);

        assertThrows(Exception.class, () -> examService.delete(id));
        verify(examRepo, never()).deleteById(anyString());
    }

    @Test
    void testFindById_ExistingExam_ReturnsExam() {
        String id = "exam1";
        Exam exam = Exam.builder().id(id).examName("First Term Exam").build();

        when(examRepo.findById(id)).thenReturn(Optional.of(exam));

        Optional<Exam> result = examService.findById(id);

        assertTrue(result.isPresent());
        assertEquals(exam, result.get());
        verify(examRepo, times(1)).findById(id);
    }

    @Test
    void testFindAll_SchoolAdmin_ReturnsAllLevelExams() {
        String token = "validToken";
        String username = "user1";
        String institutionId = "school123";
        User user = User.builder().username(username).role(Role.SCHOOL_ADMIN).build();
        ZonalEducationOffice zmoe = ZonalEducationOffice.builder().id("zmoe1").province("Province1").schoolsIds(Arrays.asList(institutionId)).build();
        ProvincialEducationOffice pmoe = ProvincialEducationOffice.builder().id("pmoe1").province("Province1").build();
        Exam schoolExam = Exam.builder().id("exam1").institutionId(institutionId).examName("School Exam").build();
        Exam zonalExam = Exam.builder().id("exam2").institutionId("zmoe1").examName("Zonal Exam").build();
        Exam provincialExam = Exam.builder().id("exam3").institutionId("pmoe1").examName("Provincial Exam").build();
        Exam nationalExam = Exam.builder().id("exam4").examName("First Term Exam").level(ExamLevel.NATIONAL).build();

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(jwtUtil.extractInstitutionId(token)).thenReturn(institutionId);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(zonalEducationOfficeRepo.findAllBySchoolsIds(institutionId)).thenReturn(zmoe);
        when(provincialEducationOfficeRepo.findByProvince("Province1")).thenReturn(pmoe);
        when(examRepo.findByInstitutionId(institutionId)).thenReturn(Arrays.asList(schoolExam));
        when(examRepo.findByInstitutionId("zmoe1")).thenReturn(Arrays.asList(zonalExam));
        when(examRepo.findByInstitutionId("pmoe1")).thenReturn(Arrays.asList(provincialExam));
        when(examRepo.findByLevelAndExamName(eq(ExamLevel.NATIONAL), anyString())).thenReturn(Arrays.asList(nationalExam));

        List<Exam> result = examService.findAll(token);

        assertEquals(4, result.size());
        verify(examRepo, times(3)).findByLevelAndExamName(eq(ExamLevel.NATIONAL), anyString());
    }

    @Test
    void testFindByGrade_ValidGrade_ReturnsExams() {
        String grade = "5";
        Exam exam = Exam.builder().id("exam1").grade("5").examName("First Term Exam").build();

        when(examRepo.findByGrade(grade)).thenReturn(Arrays.asList(exam));

        List<Exam> result = examService.findByGrade(grade, "validToken");

        assertEquals(1, result.size());
        assertEquals("5", result.get(0).getGrade());
        verify(examRepo, times(1)).findByGrade(grade);
    }

    @Test
    void testGetByGradeTermExamsToParents_ValidToken_ReturnsTermExams() {
        String token = "validToken";
        String username = "user1";
        String profileId = "parent1";
        String studentId = "student1";
        String gradeId = "grade1";
        String schoolId = "school123";
        String year = String.valueOf(LocalDate.now().getYear());
        User user = User.builder().username(username).profileId(profileId).build();
        Parent parent = Parent.builder().id(profileId).studentIds(Arrays.asList(studentId)).build();
        Student student = Student.builder().id(studentId).schoolId(schoolId).gradeId(gradeId).build();
        Grades grades = Grades.builder().id(gradeId).gradeName("5").build();
        ZonalEducationOffice zmoe = ZonalEducationOffice.builder().id("zmoe1").province("Province1").schoolsIds(Arrays.asList(schoolId)).build();
        ProvincialEducationOffice pmoe = ProvincialEducationOffice.builder().id("pmoe1").province("Province1").build();
        Exam exam = Exam.builder().id("exam1").examName("First Term Exam").grade("5").year(year).build();

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(parentRepo.findById(profileId)).thenReturn(Optional.of(parent));
        when(studentRepo.findById(studentId)).thenReturn(Optional.of(student));
        when(gradesRepo.findById(gradeId)).thenReturn(Optional.of(grades));
        when(zonalEducationOfficeRepo.findAllBySchoolsIds(schoolId)).thenReturn(zmoe);
        when(provincialEducationOfficeRepo.findByProvince("Province1")).thenReturn(pmoe);
        when(examRepo.findByGradeAndInstitutionIdAndYear("5", schoolId, year)).thenReturn(Arrays.asList(exam));
        when(examRepo.findByGradeAndInstitutionIdAndYear("5", "zmoe1", year)).thenReturn(Arrays.asList());
        when(examRepo.findByGradeAndInstitutionIdAndYear("5", "pmoe1", year)).thenReturn(Arrays.asList());
        when(examRepo.findByGradeAndLevelAndYear("5", ExamLevel.NATIONAL, year)).thenReturn(Arrays.asList());

        List<Exam> result = examService.getByGradeTermExamsToParents(token);

        assertEquals(1, result.size());
        assertEquals("First Term Exam", result.get(0).getExamName());
    }

    @Test
    void testGetByGradeTermExamsToTeacher_ValidToken_ReturnsTermExams() {
        String token = "validToken";
        String institutionId = "school123";
        String year = String.valueOf(LocalDate.now().getYear());
        ZonalEducationOffice zmoe = ZonalEducationOffice.builder().id("zmoe1").province("Province1").schoolsIds(Arrays.asList(institutionId)).build();
        ProvincialEducationOffice pmoe = ProvincialEducationOffice.builder().id("pmoe1").province("Province1").build();
        Exam exam = Exam.builder().id("exam1").examName("First Term Exam").year(year).build();

        when(jwtUtil.extractInstitutionId(token)).thenReturn(institutionId);
        when(zonalEducationOfficeRepo.findAllBySchoolsIds(institutionId)).thenReturn(zmoe);
        when(provincialEducationOfficeRepo.findByProvince("Province1")).thenReturn(pmoe);
        when(examRepo.findByInstitutionIdAndYear(institutionId, year)).thenReturn(Arrays.asList(exam));
        when(examRepo.findByInstitutionIdAndYear("zmoe1", year)).thenReturn(Arrays.asList());
        when(examRepo.findByInstitutionIdAndYear("pmoe1", year)).thenReturn(Arrays.asList());
        when(examRepo.findByLevelAndYear(ExamLevel.NATIONAL, year)).thenReturn(Arrays.asList());

        List<Exam> result = examService.getByGradeTermExamsToTeacher(token);

        assertEquals(1, result.size());
        assertEquals("First Term Exam", result.get(0).getExamName());
    }

    @Test
    void testGetByGradeALExamsToParents_ValidYear_ReturnsALExams() {
        String year = String.valueOf(LocalDate.now().getYear());
        Exam exam = Exam.builder().id("exam1").examName("G.C.E. (A/L) Examination").grade("13").level(ExamLevel.NATIONAL).year(year).build();

        when(examRepo.findByGradeAndLevelAndYearAndExamName("13", ExamLevel.NATIONAL, year, "G.C.E. (A/L) Examination")).thenReturn(Arrays.asList(exam));

        List<Exam> result = examService.getByGradeALExamsToParents();

        assertEquals(1, result.size());
        assertEquals("G.C.E. (A/L) Examination", result.get(0).getExamName());
        verify(examRepo, times(1)).findByGradeAndLevelAndYearAndExamName("13", ExamLevel.NATIONAL, year, "G.C.E. (A/L) Examination");
    }

    @Test
    void testGetByGradeOLExamsToParents_ValidYear_ReturnsOLExams() {
        String year = String.valueOf(LocalDate.now().getYear());
        Exam exam = Exam.builder().id("exam1").examName("G.C.E. (O/L) Examination").grade("11").level(ExamLevel.NATIONAL).year(year).build();

        when(examRepo.findByGradeAndLevelAndYearAndExamName("11", ExamLevel.NATIONAL, year, "G.C.E. (O/L) Examination")).thenReturn(Arrays.asList(exam));

        List<Exam> result = examService.getByGradeOLExamsToParents();

        assertEquals(1, result.size());
        assertEquals("G.C.E. (O/L) Examination", result.get(0).getExamName());
        verify(examRepo, times(1)).findByGradeAndLevelAndYearAndExamName("11", ExamLevel.NATIONAL, year, "G.C.E. (O/L) Examination");
    }

    @Test
    void testGetByGradeG5ExamsToParents_ValidYear_ReturnsG5Exams() {
        String year = String.valueOf(LocalDate.now().getYear());
        Exam exam = Exam.builder().id("exam1").examName("Grade 5 Scholarship Examination").grade("5").level(ExamLevel.NATIONAL).year(year).build();

        when(examRepo.findByGradeAndLevelAndYearAndExamName("5", ExamLevel.NATIONAL, year, "Grade 5 Scholarship Examination")).thenReturn(Arrays.asList(exam));

        List<Exam> result = examService.getByGradeG5ExamsToParents();

        assertEquals(1, result.size());
        assertEquals("Grade 5 Scholarship Examination", result.get(0).getExamName());
        verify(examRepo, times(1)).findByGradeAndLevelAndYearAndExamName("5", ExamLevel.NATIONAL, year, "Grade 5 Scholarship Examination");
    }

    @Test
    void testFindByYear_ValidYear_ReturnsExams() {
        String year = "2025";
        Exam exam = Exam.builder().id("exam1").year(year).examName("First Term Exam").build();

        when(examRepo.findByYear(year)).thenReturn(Arrays.asList(exam));

        List<Exam> result = examService.findByYear(year);

        assertEquals(1, result.size());
        assertEquals(year, result.get(0).getYear());
        verify(examRepo, times(1)).findByYear(year);
    }

    @Test
    void testCheckExamResults_NoResults_ReturnsPendingExam() {
        String token = "validToken";
        String institutionId = "school123";
        String gradeId = "grade1";
        String classId = "class1";
        String year = "2025";
        Grades grades = Grades.builder().id(gradeId).gradeName("5").build();
        ZonalEducationOffice zmoe = ZonalEducationOffice.builder().id("zmoe1").province("Province1").schoolsIds(Arrays.asList(institutionId)).build();
        ProvincialEducationOffice pmoe = ProvincialEducationOffice.builder().id("pmoe1").province("Province1").build();
        Exam exam = Exam.builder().id("exam1").examName("First Term Exam").grade("5").year(year).build();

        when(jwtUtil.extractInstitutionId(token)).thenReturn(institutionId);
        when(gradesRepo.findById(gradeId)).thenReturn(Optional.of(grades));
        when(zonalEducationOfficeRepo.findAllBySchoolsIds(institutionId)).thenReturn(zmoe);
        when(provincialEducationOfficeRepo.findByProvince("Province1")).thenReturn(pmoe);
        when(examRepo.findByGradeAndInstitutionIdAndYear("5", institutionId, year)).thenReturn(Arrays.asList(exam));
        when(examRepo.findByGradeAndInstitutionIdAndYear("5", "zmoe1", year)).thenReturn(Arrays.asList());
        when(examRepo.findByGradeAndInstitutionIdAndYear("5", "pmoe1", year)).thenReturn(Arrays.asList());
        when(examRepo.findByGradeAndLevelAndYear("5", ExamLevel.NATIONAL, year)).thenReturn(Arrays.asList());
        when(examResultsRepo.findByExamIdAndSchoolIdAndGradeIdAndClassId("exam1", institutionId, gradeId, classId)).thenReturn(null);

        Exam result = examService.checkExamResults(gradeId, year, classId, token);

        assertEquals(exam, result);
        assertEquals("First Term Exam", result.getExamName());
    }

    @Test
    void testCheckExamResults_HasResults_ReturnsNull() {
        String token = "validToken";
        String institutionId = "school123";
        String gradeId = "grade1";
        String classId = "class1";
        String year = "2025";
        Grades grades = Grades.builder().id(gradeId).gradeName("5").build();
        ZonalEducationOffice zmoe = ZonalEducationOffice.builder().id("zmoe1").province("Province1").schoolsIds(Arrays.asList(institutionId)).build();
        ProvincialEducationOffice pmoe = ProvincialEducationOffice.builder().id("pmoe1").province("Province1").build();
        Exam exam = Exam.builder().id("exam1").examName("First Term Exam").grade("5").year(year).build();
        ExamResults examResults = ExamResults.builder().id("result1").examId("exam1").build();

        when(jwtUtil.extractInstitutionId(token)).thenReturn(institutionId);
        when(gradesRepo.findById(gradeId)).thenReturn(Optional.of(grades));
        when(zonalEducationOfficeRepo.findAllBySchoolsIds(institutionId)).thenReturn(zmoe);
        when(provincialEducationOfficeRepo.findByProvince("Province1")).thenReturn(pmoe);
        when(examRepo.findByGradeAndInstitutionIdAndYear("5", institutionId, year)).thenReturn(Arrays.asList(exam));
        when(examRepo.findByGradeAndInstitutionIdAndYear("5", "zmoe1", year)).thenReturn(Arrays.asList());
        when(examRepo.findByGradeAndInstitutionIdAndYear("5", "pmoe1", year)).thenReturn(Arrays.asList());
        when(examRepo.findByGradeAndLevelAndYear("5", ExamLevel.NATIONAL, year)).thenReturn(Arrays.asList());
        when(examResultsRepo.findByExamIdAndSchoolIdAndGradeIdAndClassId("exam1", institutionId, gradeId, classId)).thenReturn(examResults);

        Exam result = examService.checkExamResults(gradeId, year, classId, token);

        assertNull(result);
    }

    @Test
    void testGetByGradeTermExamsToTeacherMyClass_ValidInput_ReturnsExamResponses() {
        String token = "validToken";
        String institutionId = "school123";
        String gradeId = "grade1";
        String year = "2025";
        String username = "user1";
        String profileId = "teacher1";
        String classId = "class1";
        User user = User.builder().username(username).profileId(profileId).build();
        ClassRoom classRoom = ClassRoom.builder().id(classId).classTeacherId(profileId).build();
        Grades grades = Grades.builder().id(gradeId).gradeName("5").build();
        ZonalEducationOffice zmoe = ZonalEducationOffice.builder().id("zmoe1").province("Province1").schoolsIds(Arrays.asList(institutionId)).build();
        ProvincialEducationOffice pmoe = ProvincialEducationOffice.builder().id("pmoe1").province("Province1").build();
        Exam exam = Exam.builder()
                .id("exam1")
                .examName("First Term Exam")
                .grade("5")
                .year(year)
                .institutionId(institutionId)
                .timetable(Arrays.asList(ExamTimetableEntry.builder().subject("Math").paper("part_1").date(LocalDate.now()).startTime(LocalTime.now()).endTime(LocalTime.now().plusHours(1)).build()))
                .build();
        SubjectResults subjectResults = SubjectResults.builder().examId("exam1").subject("Math").build();
        ExamResults examResults = ExamResults.builder().id("result1").examId("exam1").build();

        when(jwtUtil.extractInstitutionId(token)).thenReturn(institutionId);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(classRoomRepo.findByClassTeacherId(profileId)).thenReturn(classRoom);
        when(gradesRepo.findById(gradeId)).thenReturn(Optional.of(grades));
        when(zonalEducationOfficeRepo.findAllBySchoolsIds(institutionId)).thenReturn(zmoe);
        when(provincialEducationOfficeRepo.findByProvince("Province1")).thenReturn(pmoe);
        when(examRepo.findByGradeAndInstitutionIdAndYear("5", institutionId, year)).thenReturn(Arrays.asList(exam));
        when(examRepo.findByGradeAndInstitutionIdAndYear("5", "zmoe1", year)).thenReturn(Arrays.asList());
        when(examRepo.findByGradeAndInstitutionIdAndYear("5", "pmoe1", year)).thenReturn(Arrays.asList());
        when(examRepo.findByGradeAndLevelAndYear("5", ExamLevel.NATIONAL, year)).thenReturn(Arrays.asList());
        when(subjectResultsRepo.findByExamIdAndSubjectAndGradeIdAndSchoolIdAndClassIdAndYear("exam1", "Math", gradeId, institutionId, classId, year)).thenReturn(subjectResults);
        when(examResultsRepo.findByExamIdAndSchoolIdAndGradeIdAndClassId("exam1", institutionId, gradeId, classId)).thenReturn(examResults);

        List<ExamResponseToReport> result = examService.getByGradeTermExamsToTeacherMyClass(gradeId, year, token);

        assertEquals(1, result.size());
        assertEquals("exam1", result.get(0).getId());
        assertEquals("First Term Exam", result.get(0).getExamName());
        assertEquals(ExamsResults.RELEASED, result.get(0).getExamsResultsStatus());
        assertEquals(1, result.get(0).getTimetable().size());
        assertEquals(ExamsSubjectResults.RELEASED, result.get(0).getTimetable().get(0).getExamsSubjectResultsStatus());
    }
}
