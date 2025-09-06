
package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.enums.Role;
import com.smartEdu.SmartEduBackend.repo.*;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ParentRepo parentRepo;

    @Mock
    private StudentRepo studentRepo;

    @Mock
    private TeacherRepo teacherRepo;

    @Mock
    private GradesRepo gradesRepo;

    @Mock
    private ClassRoomRepo classRoomRepo;

    @Mock
    private SchoolRepo schoolRepo;

    @InjectMocks
    private StudentService studentService;

    private Student student;
    private StudentResponse studentResponse;
    private User user;
    private Parent parent;
    private ClassRoom classRoom;
    private Grades grade;
    private School school;
    private String token;

    @BeforeEach
    void setUp() {
        token = "test-token";

        student = Student.builder()
                .id("STU001")
                .entryDate(LocalDate.of(2025, 1, 1))
                .fullName("John Smith")
                .fullNameWithInitials("J. Smith")
                .dateOfBirth(LocalDate.of(2010, 5, 15))
                .motherName("Jane Smith")
                .motherContact("1234567890")
                .fatherName("John Doe")
                .fatherContact("0987654321")
                .address("123 Main St")
                .registrationNumber("SCH-00001-2025-0001")
                .gradeId("GRD001")
                .schoolId("SCH001")
                .classId("CLS001")
                .achievements(new ArrayList<>())
                .build();

        studentResponse = StudentResponse.builder()
                .id("STU001")
                .entryDate(LocalDate.of(2025, 1, 1))
                .fullName("John Smith")
                .fullNameWithInitials("J. Smith")
                .dateOfBirth(LocalDate.of(2010, 5, 15))
                .motherName("Jane Smith")
                .motherContact("1234567890")
                .fatherName("John Doe")
                .fatherContact("0987654321")
                .address("123 Main St")
                .registrationNumber("SCH-00001-2025-0001")
                .gradeId("GRD001")
                .gradeName("Grade 11")
                .schoolId("SCH001")
                .classId("CLS001")
                .className("11A")
                .achievements(new ArrayList<>())
                .build();

        user = User.builder()
                .id("USER001")
                .username("teacher1")
                .profileId("TCH001")
                .role(Role.TEACHER)
                .institutionID("SCH001")
                .build();

        parent = Parent.builder()
                .id("PAR001")
                .studentIds(List.of("STU001"))
                .build();

        classRoom = ClassRoom.builder()
                .id("CLS001")
                .className("11A")
                .classTeacherId("TCH001")
                .studentIds(new ArrayList<>(List.of("STU001")))
                .build();

        grade = Grades.builder()
                .id("GRD001")
                .gradeName("Grade 11")
                .build();

        school = School.builder()
                .id("SCH001")
                .schoolNumber("SCH-00001")
                .schoolName("Test School")
                .build();
    }

    @Test
    void testSave_Success() {
        when(studentRepo.findByRegistrationNumber(student.getRegistrationNumber())).thenReturn(Optional.empty());
        when(jwtUtil.extractInstitutionId(token)).thenReturn("SCH001");
        when(studentRepo.save(any(Student.class))).thenReturn(student);
        when(classRoomRepo.findById("CLS001")).thenReturn(Optional.of(classRoom));
        when(classRoomRepo.save(any(ClassRoom.class))).thenReturn(classRoom);

        Student result = studentService.save(student, token);

        assertNotNull(result);
        assertEquals("STU001", result.getId());
        assertEquals("SCH001", result.getSchoolId());
        verify(studentRepo, times(1)).save(any(Student.class));
        verify(classRoomRepo, times(1)).save(any(ClassRoom.class));
    }

    @Test
    void testSave_RegistrationNumberExists() {
        when(studentRepo.findByRegistrationNumber(student.getRegistrationNumber())).thenReturn(Optional.of(student));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentService.save(student, token);
        });

        assertEquals("Registration number already exists!", exception.getMessage());
        verify(studentRepo, never()).save(any(Student.class));
    }

    @Test
    void testUpdate_Success() {
        Student updatedStudent = Student.builder()
                .id("STU001")
                .fullName("John Smith Updated")
                .build();

        when(studentRepo.findById("STU001")).thenReturn(Optional.of(student));
        when(studentRepo.save(any(Student.class))).thenReturn(updatedStudent);

        Student result = studentService.update("STU001", updatedStudent);

        assertNotNull(result);
        assertEquals("John Smith Updated", result.getFullName());
        verify(studentRepo, times(1)).save(any(Student.class));
    }

    @Test
    void testUpdate_StudentNotFound() {
        when(studentRepo.findById("STU001")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentService.update("STU001", student);
        });

        assertEquals("Student not found!", exception.getMessage());
        verify(studentRepo, never()).save(any(Student.class));
    }

    @Test
    void testDelete_Success() {
        when(studentRepo.findById("STU001")).thenReturn(Optional.of(student));

        studentService.delete("STU001");

        verify(studentRepo, times(1)).deleteById("STU001");
    }

    @Test
    void testDelete_StudentNotFound() {
        when(studentRepo.findById("STU001")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentService.delete("STU001");
        });

        assertEquals("Student not found!", exception.getMessage());
        verify(studentRepo, never()).deleteById(anyString());
    }

    @Test
    void testFindById_Success() {
        when(studentRepo.findById("STU001")).thenReturn(Optional.of(student));

        Optional<Student> result = studentService.findById("STU001");

        assertTrue(result.isPresent());
        assertEquals("John Smith", result.get().getFullName());
    }

    @Test
    void testFindClassAllStudentsByClassId_Success() {
        when(classRoomRepo.findById("CLS001")).thenReturn(Optional.of(classRoom));
        when(studentRepo.findById("STU001")).thenReturn(Optional.of(student));

        List<StudentResponse> result = studentService.findClassAllStudentsByClassId("CLS001");

        assertEquals(1, result.size());
        assertEquals("J. Smith", result.get(0).getFullNameWithInitials());
        assertEquals("SCH-00001-2025-0001", result.get(0).getRegistrationNumber());
    }

    @Test
    void testFindMyClassAllStudents_Success() {
        when(jwtUtil.extractUsername(token)).thenReturn("teacher1");
        when(userRepo.findByUsername("teacher1")).thenReturn(Optional.of(user));
        when(classRoomRepo.findByClassTeacherId("TCH001")).thenReturn(classRoom);
        when(studentRepo.findById("STU001")).thenReturn(Optional.of(student));
        when(gradesRepo.findById("GRD001")).thenReturn(Optional.of(grade));
        when(classRoomRepo.findById("CLS001")).thenReturn(Optional.of(classRoom));

        List<StudentResponse> result = studentService.findMyClassAllStudents(token);

        assertEquals(1, result.size());
        assertEquals("Grade 11", result.get(0).getGradeName());
        assertEquals("11A", result.get(0).getClassName());
    }

    @Test
    void testGetStudentByStudentId_Success() {
        when(studentRepo.findById("STU001")).thenReturn(Optional.of(student));
        when(gradesRepo.findById("GRD001")).thenReturn(Optional.of(grade));
        when(classRoomRepo.findById("CLS001")).thenReturn(Optional.of(classRoom));

        StudentResponse result = studentService.getStudentByStudentId("STU001", token);

        assertNotNull(result);
        assertEquals("J. Smith", result.getFullNameWithInitials());
        assertEquals("Grade 11", result.getGradeName());
        assertEquals("11A", result.getClassName());
    }

    @Test
    void testFindStudentToParent_Success() {
        when(jwtUtil.extractUsername(token)).thenReturn("teacher1");
        when(userRepo.findByUsername("teacher1")).thenReturn(Optional.of(user));
        when(parentRepo.findById("PAR001")).thenReturn(Optional.of(parent));
        when(studentRepo.findById("STU001")).thenReturn(Optional.of(student));
        when(gradesRepo.findById("GRD001")).thenReturn(Optional.of(grade));
        when(classRoomRepo.findById("CLS001")).thenReturn(Optional.of(classRoom));

        StudentResponse result = studentService.findStudentToParent(token);

        assertNotNull(result);
        assertEquals("J. Smith", result.getFullNameWithInitials());
        assertEquals("Grade 11", result.getGradeName());
        assertEquals("11A", result.getClassName());
    }

    @Test
    void testFindAll_Success() {
        when(jwtUtil.extractInstitutionId(token)).thenReturn("SCH001");
        when(studentRepo.findAllBySchoolId("SCH001")).thenReturn(List.of(student));
        when(gradesRepo.findById("GRD001")).thenReturn(Optional.of(grade));
        when(classRoomRepo.findById("CLS001")).thenReturn(Optional.of(classRoom));

        List<StudentResponse> result = studentService.findAll(0, 10, token);

        assertEquals(1, result.size());
        assertEquals("J. Smith", result.get(0).getFullNameWithInitials());
        assertEquals("Grade 11", result.get(0).getGradeName());
        assertEquals("11A", result.get(0).getClassName());
    }

    @Test
    void testSearchStudentsByName_Grade11_Success() {
        Grades grade11 = Grades.builder().id("GRD001").gradeName("11").build();
        when(jwtUtil.extractInstitutionId(token)).thenReturn("SCH001");
        when(gradesRepo.findByGradeName("11")).thenReturn(grade11);
        when(studentRepo.findAllBySchoolIdAndFullNameAndGradeId("SCH001", "John", "GRD001")).thenReturn(List.of(student));

        List<Student> result = studentService.searchStudentsByName(token, "John", "ol");

        assertEquals(1, result.size());
        assertEquals("J. Smith", result.get(0).getFullNameWithInitials());
    }

    @Test
    void testSearchStudentsByName_Grade13_Success() {
        Grades scienceGrade = Grades.builder().id("GRD002").gradeName("13(Science)").build();
        when(jwtUtil.extractInstitutionId(token)).thenReturn("SCH001");
        when(gradesRepo.findByGradeName("13(Science)")).thenReturn(scienceGrade);
        when(studentRepo.findAllBySchoolIdAndFullNameAndGradeId("SCH001", "John", "GRD002")).thenReturn(List.of(student));
        when(gradesRepo.findByGradeName("13(Commerce)")).thenReturn(Grades.builder().id("GRD003").gradeName("13(Commerce)").build());
        when(gradesRepo.findByGradeName("13(Tech)")).thenReturn(Grades.builder().id("GRD004").gradeName("13(Tech)").build());
        when(gradesRepo.findByGradeName("13(Arts)")).thenReturn(Grades.builder().id("GRD005").gradeName("13(Arts)").build());

        List<Student> result = studentService.searchStudentsByName(token, "John", "al");

        assertEquals(1, result.size());
        assertEquals("J. Smith", result.get(0).getFullNameWithInitials());
    }

    @Test
    void testSearchStudentsByName_InvalidApplication() {
        List<Student> result = studentService.searchStudentsByName(token, "John", "invalid");

        assertTrue(result.isEmpty());
    }

    @Test
    void testGenerateRegistrationNumber_Success() {
        when(jwtUtil.extractInstitutionId(token)).thenReturn("SCH001");
        when(schoolRepo.findById("SCH001")).thenReturn(Optional.of(school));
        when(studentRepo.countBySchoolIdAndEntryDateBetween(eq("SCH001"), any(LocalDate.class), any(LocalDate.class))).thenReturn(0L);

        String result = studentService.generateRegistrationNumber(token);

        assertEquals("SCH-00001-" + Year.now().getValue() + "-0001", result);
    }

    @Test
    void testGenerateRegistrationNumber_SchoolNotFound() {
        when(jwtUtil.extractInstitutionId(token)).thenReturn("SCH001");
        when(schoolRepo.findById("SCH001")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentService.generateRegistrationNumber(token);
        });

        assertEquals("School not found", exception.getMessage());
    }
}
