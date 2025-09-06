package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.repo.*;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClassRoomServiceTests {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private GradesRepo gradesRepo;

    @Mock
    private ExamRepo examRepo;

    @Mock
    private StudentReportRepo studentReportRepo;

    @Mock
    private StudentRepo studentRepo;

    @Mock
    private ClassRoomRepo classRoomRepo;

    @InjectMocks
    private ClassRoomService classRoomService;

    private ClassRoom classRoom;
    private Grades grades;
    private StudentReport studentReport1;
    private StudentReport studentReport2;
    private Report report1;
    private Report report2;

    @BeforeEach
    void setUp() {
        // Initialize test data
        classRoom = ClassRoom.builder()
                .id("class1")
                .className("Class A")
                .gradeId("grade1")
                .classTeacherId("teacher1")
                .classTeacherSubject("Math")
                .studentIds(Arrays.asList("student1", "student2"))
                .build();

        grades = Grades.builder()
                .id("grade1")
                .schoolId("school1")
                .gradeName("1")
                .classIds(new ArrayList<>(Arrays.asList("class1")))
                .build();

        report1 = Report.builder()
                .year(String.valueOf(LocalDate.now().getYear() - 1))
                .examName("Final Term Exam")
                .rank(1)
                .build();

        report2 = Report.builder()
                .year(String.valueOf(LocalDate.now().getYear() - 1))
                .examName("Final Term Exam")
                .rank(2)
                .build();

        studentReport1 = StudentReport.builder()
                .studentId("student1")
                .reports(Arrays.asList(report1))
                .build();

        studentReport2 = StudentReport.builder()
                .studentId("student2")
                .reports(Arrays.asList(report2))
                .build();
    }

    // Test case for createClass - Success
    @Test
    void shouldCreateClassSuccessfully() {
        when(gradesRepo.findById("grade1")).thenReturn(Optional.of(grades));
        when(classRoomRepo.save(any(ClassRoom.class))).thenReturn(classRoom);
        when(gradesRepo.save(any(Grades.class))).thenReturn(grades);

        ClassRoom result = classRoomService.createClass(classRoom, "token123");

        assertNotNull(result);
        assertEquals("class1", result.getId());
        assertEquals("Class A", result.getClassName());
        verify(gradesRepo, times(1)).findById("grade1");
        verify(gradesRepo, times(1)).save(grades);
        verify(classRoomRepo, times(2)).save(any(ClassRoom.class));
        assertTrue(grades.getClassIds().contains("class1"));
    }

    // Test case for createClass - Grade not found
    @Test
    void shouldThrowExceptionWhenGradeNotFound() {
        when(gradesRepo.findById("grade1")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            classRoomService.createClass(classRoom, "token123");
        });

        verify(gradesRepo, times(1)).findById("grade1");
        verify(classRoomRepo, never()).save(any(ClassRoom.class));
        verify(gradesRepo, never()).save(any(Grades.class));
    }

    // Test case for createMultipleClasses - Success
    @Test
    void shouldCreateMultipleClassesSuccessfully() {
        List<ClassRoom> classRooms = Arrays.asList(classRoom, ClassRoom.builder().id("class2").build());
        when(classRoomRepo.saveAll(classRooms)).thenReturn(classRooms);

        List<ClassRoom> result = classRoomService.createMultipleClasses(classRooms);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("class1", result.get(0).getId());
        verify(classRoomRepo, times(1)).saveAll(classRooms);
    }

    // Test case for getAllClasses - Success
    @Test
    void shouldGetAllClassesSuccessfully() {
        List<ClassRoom> classRooms = Arrays.asList(classRoom);
        when(classRoomRepo.findAll()).thenReturn(classRooms);

        List<ClassRoom> result = classRoomService.getAllClasses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("class1", result.get(0).getId());
        verify(classRoomRepo, times(1)).findAll();
    }

    // Test case for getClassById - Success
    @Test
    void shouldGetClassByIdSuccessfully() {
        when(classRoomRepo.findById("class1")).thenReturn(Optional.of(classRoom));

        ClassRoom result = classRoomService.getClassById("class1");

        assertNotNull(result);
        assertEquals("class1", result.getId());
        verify(classRoomRepo, times(1)).findById("class1");
    }

    // Test case for getClassById - Not found
    @Test
    void shouldReturnNullWhenClassNotFound() {
        when(classRoomRepo.findById("class1")).thenReturn(Optional.empty());

        ClassRoom result = classRoomService.getClassById("class1");

        assertNull(result);
        verify(classRoomRepo, times(1)).findById("class1");
    }

    // Test case for getClassesByGrade - Success
    @Test
    void shouldGetClassesByGradeSuccessfully() {
        List<ClassRoom> classRooms = Arrays.asList(classRoom);
        when(classRoomRepo.findByGradeId("grade1")).thenReturn(classRooms);

        List<ClassRoom> result = classRoomService.getClassesByGrade("grade1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("class1", result.get(0).getId());
        verify(classRoomRepo, times(1)).findByGradeId("grade1");
    }

    // Test case for updateClass - Success
    @Test
    void shouldUpdateClassSuccessfully() {
        ClassRoom updated = ClassRoom.builder()
                .id("class1")
                .className("Class B")
                .classTeacherId("teacher2")
                .build();
        when(classRoomRepo.findById("class1")).thenReturn(Optional.of(classRoom));
        when(classRoomRepo.save(any(ClassRoom.class))).thenReturn(updated);

        ClassRoom result = classRoomService.updateClass("class1", updated);

        assertNotNull(result);
        assertEquals("Class B", result.getClassName());
        assertEquals("teacher2", result.getClassTeacherId());
        verify(classRoomRepo, times(1)).findById("class1");
        verify(classRoomRepo, times(1)).save(any(ClassRoom.class));
    }

    // Test case for updateClass - Class not found
    @Test
    void shouldThrowExceptionWhenClassNotFoundOnUpdate() {
        when(classRoomRepo.findById("class1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            classRoomService.updateClass("class1", classRoom);
        }, "Class not found!");

        verify(classRoomRepo, times(1)).findById("class1");
        verify(classRoomRepo, never()).save(any(ClassRoom.class));
    }

    // Test case for deleteClass - Success
    @Test
    void shouldDeleteClassSuccessfully() {
        when(classRoomRepo.findById("class1")).thenReturn(Optional.of(classRoom));
        doNothing().when(classRoomRepo).deleteById("class1");

        classRoomService.deleteClass("class1");

        verify(classRoomRepo, times(1)).findById("class1");
        verify(classRoomRepo, times(1)).deleteById("class1");
    }

    // Test case for deleteClass - Class not found
    @Test
    void shouldThrowExceptionWhenClassNotFoundOnDelete() {
        when(classRoomRepo.findById("class1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            classRoomService.deleteClass("class1");
        }, "Class not found!");

        verify(classRoomRepo, times(1)).findById("class1");
        verify(classRoomRepo, never()).deleteById(anyString());
    }

    // Test case for classesReshuffle - Success
    @Test
    void shouldReshuffleClassesSuccessfully() {
        String token = "token123";
        when(jwtUtil.extractInstitutionId(token)).thenReturn("school1");

        // Mock grades for grade 1
        Grades grade = Grades.builder()
                .id("grade1")
                .schoolId("school1")
                .gradeName("1")
                .classIds(Arrays.asList("class1", "class2"))
                .build();
        when(gradesRepo.findAllBySchoolIdAndGradeName("school1", "1")).thenReturn(grade);

        // Mock classes
        ClassRoom classRoom2 = ClassRoom.builder()
                .id("class2")
                .className("Class B")
                .gradeId("grade1")
                .studentIds(Arrays.asList("student3", "student4"))
                .build();
        when(classRoomRepo.findById("class1")).thenReturn(Optional.of(classRoom));
        when(classRoomRepo.findById("class2")).thenReturn(Optional.of(classRoom2));

        // Mock student reports
        when(studentReportRepo.findByStudentId("student1")).thenReturn(studentReport1);
        when(studentReportRepo.findByStudentId("student2")).thenReturn(studentReport2);
        when(studentReportRepo.findByStudentId("student3")).thenReturn(
                StudentReport.builder().studentId("student3").reports(Arrays.asList(
                        Report.builder().year(String.valueOf(LocalDate.now().getYear() - 1)).examName("Final Term Exam").rank(3).build()
                )).build()
        );
        when(studentReportRepo.findByStudentId("student4")).thenReturn(null); // No report, should be skipped

        // Mock students
        when(studentRepo.findById("student1")).thenReturn(Optional.of(
                Student.builder().id("student1").classId("class1").build()
        ));
        when(studentRepo.findById("student2")).thenReturn(Optional.of(
                Student.builder().id("student2").classId("class1").build()
        ));
        when(studentRepo.findById("student3")).thenReturn(Optional.of(
                Student.builder().id("student3").classId("class2").build()
        ));
        when(studentRepo.findById("student4")).thenReturn(Optional.of(
                Student.builder().id("student4").classId("class2").build()
        ));

        // Mock save operations
        when(classRoomRepo.save(any(ClassRoom.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(studentRepo.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = classRoomService.classesReshuffle(token);

        assertTrue(result);
        verify(jwtUtil, times(1)).extractInstitutionId(token);
        verify(gradesRepo, times(10)).findAllBySchoolIdAndGradeName(eq("school1"), anyString());
        verify(classRoomRepo, times(2)).findById(anyString());
        verify(studentReportRepo, times(4)).findByStudentId(anyString());
        verify(classRoomRepo, times(2)).save(any(ClassRoom.class));
        verify(studentRepo, atLeast(3)).save(any(Student.class));
    }

    // Test case for classesReshuffle - No valid students
    @Test
    void shouldHandleNoValidStudentsInReshuffle() {
        String token = "token123";
        when(jwtUtil.extractInstitutionId(token)).thenReturn("school1");

        Grades grade = Grades.builder()
                .id("grade1")
                .schoolId("school1")
                .gradeName("1")
                .classIds(Arrays.asList("class1"))
                .build();
        when(gradesRepo.findAllBySchoolIdAndGradeName("school1", "1")).thenReturn(grade);

        when(classRoomRepo.findById("class1")).thenReturn(Optional.of(classRoom));
        when(studentReportRepo.findByStudentId("student1")).thenReturn(null);
        when(studentReportRepo.findByStudentId("student2")).thenReturn(null);

        boolean result = classRoomService.classesReshuffle(token);

        assertTrue(result);
        verify(jwtUtil, times(1)).extractInstitutionId(token);
        verify(gradesRepo, times(10)).findAllBySchoolIdAndGradeName(eq("school1"), anyString());
        verify(classRoomRepo, times(1)).findById("class1");
        verify(studentReportRepo, times(2)).findByStudentId(anyString());
        verify(classRoomRepo, times(1)).save(any(ClassRoom.class));
        verify(studentRepo, times(2)).save(any(Student.class));
    }

    // Test case for classesReshuffle - No classes in grade
    @Test
    void shouldHandleNoClassesInGrade() {
        String token = "token123";
        when(jwtUtil.extractInstitutionId(token)).thenReturn("school1");
        when(gradesRepo.findAllBySchoolIdAndGradeName(eq("school1"), anyString())).thenReturn(null);

        boolean result = classRoomService.classesReshuffle(token);

        assertTrue(result);
        verify(jwtUtil, times(1)).extractInstitutionId(token);
        verify(gradesRepo, times(10)).findAllBySchoolIdAndGradeName(eq("school1"), anyString());
        verify(classRoomRepo, never()).findById(anyString());
        verify(studentReportRepo, never()).findByStudentId(anyString());
        verify(classRoomRepo, never()).save(any(ClassRoom.class));
        verify(studentRepo, never()).save(any(Student.class));
    }
}
