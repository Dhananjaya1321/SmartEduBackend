package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.enums.ExamsResults;
import com.smartEdu.SmartEduBackend.repo.*;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExamResultsServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepo userRepo;

    @Mock
    private NationalLevelExamsResultsRepo nationalLevelExamsResultsRepo;

    @Mock
    private ParentRepo parentRepo;

    @Mock
    private GradesRepo gradesRepo;

    @Mock
    private ClassRoomRepo classRoomRepo;

    @Mock
    private ExamResultsRepo examResultsRepo;

    @Mock
    private ExamRepo examRepo;

    @Mock
    private SubjectResultsRepo subjectResultsRepo;

    @Mock
    private StudentReportRepo studentReportRepo;

    @Mock
    private StudentRepo studentRepo;

    @InjectMocks
    private ExamResultsService examResultsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave_ValidSubjectResultsAndToken_SavesSubjectResults() {
        String token = "validToken";
        String institutionId = "school123";
        SubjectResults subjectResults = SubjectResults.builder().subject("Math").build();
        SubjectResults savedSubjectResults = SubjectResults.builder().subject("Math").schoolId(institutionId).build();

        when(jwtUtil.extractInstitutionId(token)).thenReturn(institutionId);
        when(subjectResultsRepo.save(any(SubjectResults.class))).thenReturn(savedSubjectResults);

        SubjectResults result = examResultsService.save(subjectResults, token);

        assertEquals(savedSubjectResults, result);
        assertEquals(institutionId, result.getSchoolId());
        verify(subjectResultsRepo, times(1)).save(subjectResults);
    }

    @Test
    void testSaveExamResults_ValidInput_SavesExamResultsAndReports() {
        String token = "validToken";
        String institutionId = "school123";
        String username = "user1";
        String profileId = "teacher1";
        String classId = "class1";
        String gradeId = "grade1";
        String examId = "exam1";
        String year = "2025";
        String studentId = "student1";
        String gradeName = "5";

        User user = User.builder().username(username).profileId(profileId).build();
        ClassRoom classRoom = ClassRoom.builder().id(classId).classTeacherId(profileId).studentIds(List.of(studentId)).build();
        Grades grade = Grades.builder().id(gradeId).gradeName(gradeName).build();
        ExamResults examResults = ExamResults.builder().examId(examId).examName("First Term Exam").gradeName(gradeName).year(year).build();
        Exam exam = Exam.builder().id(examId).year(year).timetable(List.of(
                ExamTimetableEntry.builder().subject("Math").build()
        )).build();
        Student student = Student.builder().id(studentId).fullNameWithInitials("John Doe").build();
        StudentReport studentReport = null;
        SubjectResults subjectResults = SubjectResults.builder()
                .examId(examId).subject("Math").gradeId(gradeId).schoolId(institutionId).classId(classId).year(year)
                .students(List.of(StudentWithSubjectMarks.builder().studentId(studentId).marks(90.0).build()))
                .build();

        when(jwtUtil.extractInstitutionId(token)).thenReturn(institutionId);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(classRoomRepo.findByClassTeacherId(profileId)).thenReturn(classRoom);
        when(gradesRepo.findByGradeNameAndSchoolId(gradeName, institutionId)).thenReturn(grade);
        when(examRepo.findById(examId)).thenReturn(Optional.of(exam));
        when(studentRepo.findById(studentId)).thenReturn(Optional.of(student));
        when(studentReportRepo.findByStudentId(studentId)).thenReturn(studentReport);
        when(subjectResultsRepo.findByExamIdAndSubjectAndGradeIdAndSchoolIdAndClassIdAndYear(
                examId, "Math", gradeId, institutionId, classId, year)).thenReturn(subjectResults);
        when(examResultsRepo.save(any(ExamResults.class))).thenReturn(examResults);
        when(studentReportRepo.save(any(StudentReport.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExamResults result = examResultsService.saveExamResults(examResults, token);

        assertEquals(examResults, result);
        assertEquals(ExamsResults.RELEASED, result.getExamResultsStatus());
        assertEquals(classId, result.getClassId());
        assertEquals(gradeId, result.getGradeId());
        verify(studentReportRepo, times(1)).save(any(StudentReport.class));
        verify(examResultsRepo, times(1)).save(examResults);
    }

    @Test
    void testSaveExamResults_UserNotFound_ThrowsRuntimeException() {
        String token = "validToken";
        ExamResults examResults = ExamResults.builder().examId("exam1").examName("First Term Exam").gradeName("5").year("2025").build();

        when(jwtUtil.extractUsername(token)).thenReturn("user1");
        when(userRepo.findByUsername("user1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> examResultsService.saveExamResults(examResults, token));
        verify(examResultsRepo, never()).save(any(ExamResults.class));
    }

    @Test
    void testGetAllClassStudentsResultsDetails_ValidInput_ReturnsReportResponses() {
        String token = "validToken";
        String username = "user1";
        String profileId = "teacher1";
        String classId = "class1";
        String examId = "exam1";
        String studentId = "student1";
        String year = "2025";
        User user = User.builder().username(username).profileId(profileId).build();
        ClassRoom classRoom = ClassRoom.builder().id(classId).classTeacherId(profileId).studentIds(List.of(studentId)).build();
        StudentReport studentReport = StudentReport.builder()
                .studentId(studentId).studentName("John Doe").schoolId("school123")
                .reports(List.of(Report.builder()
                        .examId(examId).examName("First Term Exam").year(year).gradeId("grade1").gradeName("5")
                        .totalMarks(90.0).averageMarks(90.0).rank(1)
                        .marksList(List.of(Marks.builder().subject("Math").marks(90.0).build()))
                        .build()))
                .build();

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(classRoomRepo.findByClassTeacherId(profileId)).thenReturn(classRoom);
        when(studentReportRepo.findByStudentId(studentId)).thenReturn(studentReport);

        List<ReportResponse> result = examResultsService.getAllClassStudentsResultsDetails(examId, token);

        assertEquals(1, result.size());
        assertEquals(studentId, result.get(0).getStudentId());
        assertEquals("John Doe", result.get(0).getStudentName());
        assertEquals(examId, result.get(0).getExamId());
        assertEquals(90.0, result.get(0).getTotalMarks());
        assertEquals(1, result.get(0).getRank());
    }

    @Test
    void testGetStudentsResultsDetailsToTeacher_ValidInput_ReturnsFilteredReports() {
        String studentId = "student1";
        String token = "validToken";
        String year = String.valueOf(LocalDate.now().getYear());
        StudentReport studentReport = StudentReport.builder()
                .studentId(studentId).studentName("John Doe")
                .reports(List.of(
                        Report.builder().examId("exam1").examName("First Term Exam").year(year).build(),
                        Report.builder().examId("exam2").examName("Other Exam").year(year).build()
                )).build();

        when(studentReportRepo.findByStudentId(studentId)).thenReturn(studentReport);

        StudentReport result = examResultsService.getStudentsResultsDetailsToTeacher(studentId, token);

        assertEquals(studentId, result.getStudentId());
        assertEquals(1, result.getReports().size());
        assertEquals("First Term Exam", result.getReports().get(0).getExamName());
    }

    @Test
    void testGetStudentsResultsDetailsToParents_ValidInput_ReturnsFilteredReports() {
        String token = "validToken";
        String username = "user1";
        String profileId = "parent1";
        String studentId = "student1";
        String year = String.valueOf(LocalDate.now().getYear());
        User user = User.builder().username(username).profileId(profileId).build();
        Parent parent = Parent.builder().id(profileId).studentIds(List.of(studentId)).build();
        Student student = Student.builder().id(studentId).build();
        StudentReport studentReport = StudentReport.builder()
                .studentId(studentId).studentName("John Doe")
                .reports(List.of(
                        Report.builder().examId("exam1").examName("First Term Exam").year(year).build(),
                        Report.builder().examId("exam2").examName("Other Exam").year(year).build()
                )).build();

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(parentRepo.findById(profileId)).thenReturn(Optional.of(parent));
        when(studentRepo.findById(studentId)).thenReturn(Optional.of(student));
        when(studentReportRepo.findByStudentId(studentId)).thenReturn(studentReport);

        StudentReport result = examResultsService.getStudentsResultsDetailsToParents(token);

        assertEquals(studentId, result.getStudentId());
        assertEquals(1, result.getReports().size());
        assertEquals("First Term Exam", result.getReports().get(0).getExamName());
    }

    @Test
    void testGetAllClassStudentsResultsDetailsToParents_ValidInput_ReturnsReportResponses() {
        String token = "validToken";
        String username = "user1";
        String profileId = "parent1";
        String studentId = "student1";
        String classId = "class1";
        String examId = "exam1";
        String year = String.valueOf(LocalDate.now().getYear());
        User user = User.builder().username(username).profileId(profileId).build();
        Parent parent = Parent.builder().id(profileId).studentIds(List.of(studentId)).build();
        Student student = Student.builder().id(studentId).classId(classId).build();
        ClassRoom classRoom = ClassRoom.builder().id(classId).studentIds(List.of(studentId)).build();
        StudentReport studentReport = StudentReport.builder()
                .studentId(studentId).studentName("John Doe")
                .reports(List.of(Report.builder()
                        .examId(examId).examName("First Term Exam").year(year).gradeId("grade1").gradeName("5")
                        .totalMarks(90.0).averageMarks(90.0).rank(1)
                        .marksList(List.of(Marks.builder().subject("Math").marks(90.0).build()))
                        .build()))
                .build();

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(parentRepo.findById(profileId)).thenReturn(Optional.of(parent));
        when(studentRepo.findById(studentId)).thenReturn(Optional.of(student));
        when(classRoomRepo.findById(classId)).thenReturn(Optional.of(classRoom));
        when(studentReportRepo.findByStudentId(studentId)).thenReturn(studentReport);

        List<ReportResponse> result = examResultsService.getAllClassStudentsResultsDetailsToParents(token);

        assertEquals(1, result.size());
        assertEquals(studentId, result.get(0).getStudentId());
        assertEquals("John Doe", result.get(0).getStudentName());
        assertEquals(examId, result.get(0).getExamId());
        assertEquals(90.0, result.get(0).getTotalMarks());
    }

    @Test
    void testGetMyChildData_ValidInput_ReturnsReportResponse() {
        String token = "validToken";
        String username = "user1";
        String profileId = "parent1";
        String studentId = "student1";
        String examId = "exam1";
        String year = "2025";
        User user = User.builder().username(username).profileId(profileId).build();
        Parent parent = Parent.builder().id(profileId).studentIds(List.of(studentId)).build();
        Student student = Student.builder().id(studentId).build();
        StudentReport studentReport = StudentReport.builder()
                .studentId(studentId).studentName("John Doe")
                .reports(List.of(Report.builder()
                        .examId(examId).examName("First Term Exam").year(year).gradeId("grade1").gradeName("5")
                        .totalMarks(90.0).averageMarks(90.0).rank(1)
                        .marksList(List.of(Marks.builder().subject("Math").marks(90.0).build()))
                        .build()))
                .build();

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(parentRepo.findById(profileId)).thenReturn(Optional.of(parent));
        when(studentRepo.findById(studentId)).thenReturn(Optional.of(student));
        when(studentReportRepo.findByStudentId(studentId)).thenReturn(studentReport);

        ReportResponse result = examResultsService.getMyChildData(examId, token);

        assertEquals(studentId, result.getStudentId());
        assertEquals("John Doe", result.getStudentName());
        assertEquals(examId, result.getExamId());
        assertEquals(90.0, result.getTotalMarks());
        assertEquals(1, result.getRank());
    }

    @Test
    void testGetNationalLevelExamsResults_ValidInput_ReturnsResults() {
        String indexNumber = "12345";
        String examName = "al";
        String year = "2025";
        String expectedExamName = "G.C.E. (A/L) Examination";
        NationalLevelExamsResults results = NationalLevelExamsResults.builder()
                .indexNumber(indexNumber).examName(expectedExamName).year(year).build();

        when(nationalLevelExamsResultsRepo.findByIndexNumberAndExamNameAndYear(indexNumber, expectedExamName, year))
                .thenReturn(results);

        NationalLevelExamsResults result = examResultsService.getNationalLevelExamsResults(indexNumber, examName, year);

        assertEquals(results, result);
        assertEquals(expectedExamName, result.getExamName());
        verify(nationalLevelExamsResultsRepo, times(1)).findByIndexNumberAndExamNameAndYear(indexNumber, expectedExamName, year);
    }

    @Test
    void testGetNationalLevelExamsResults_Grade5Exam_ReturnsResults() {
        String indexNumber = "12345";
        String examName = "g5";
        String year = "2025";
        String expectedExamName = "Grade 5 Scholarship Examination";
        NationalLevelExamsResults results = NationalLevelExamsResults.builder()
                .indexNumber(indexNumber).examName(expectedExamName).year(year).build();

        when(nationalLevelExamsResultsRepo.findByIndexNumberAndExamNameAndYear(indexNumber, expectedExamName, year))
                .thenReturn(results);

        NationalLevelExamsResults result = examResultsService.getNationalLevelExamsResults(indexNumber, examName, year);

        assertEquals(results, result);
        assertEquals(expectedExamName, result.getExamName());
        verify(nationalLevelExamsResultsRepo, times(1)).findByIndexNumberAndExamNameAndYear(indexNumber, expectedExamName, year);
    }
}
