package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.enums.AttendanceStatus;
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
public class AttendanceServiceTests {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ClassRoomRepo classRoomRepo;

    @Mock
    private TeacherRepo teacherRepo;

    @Mock
    private ParentRepo parentRepo;

    @Mock
    private AttendanceRepo attendanceRepository;

    @Mock
    private StudentRepo studentRepo;

    @InjectMocks
    private AttendanceService attendanceService;

    private AttendanceRequest attendanceRequest;
    private Attendance attendance;
    private Student student;
    private Parent parent;
    private User user;
    private ClassRoom classRoom;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        // Initialize test data
        attendance = Attendance.builder()
                .id("att1")
                .studentId("student1")
                .classId("class1")
                .date(LocalDate.now())
                .status(AttendanceStatus.PRESENT)
                .build();

        attendanceRequest = new AttendanceRequest();
        attendanceRequest.setClassId("class1");
        attendanceRequest.setDate(LocalDate.now());
        attendanceRequest.setAttendance(Arrays.asList(
                AttendanceStudentIdAndStatus.builder().studentId("student1").status(AttendanceStatus.PRESENT).build(),
                AttendanceStudentIdAndStatus.builder().studentId("student2").status(AttendanceStatus.ABSENT).build()
        ));

        student = Student.builder()
                .id("student1")
                .fullNameWithInitials("John Doe")
                .classId("class1")
                .build();

        parent = Parent.builder()
                .id("parent1")
                .studentIds(Arrays.asList("student1"))
                .build();

        user = User.builder()
                .username("parent_user")
                .profileId("parent1")
                .contact("1234567890")
                .build();

        classRoom = ClassRoom.builder()
                .id("class1")
                .classTeacherId("teacher1")
                .build();

        teacher = Teacher.builder()
                .id("teacher1")
                .build();
    }

    // Test case for saveAttendance - Success
    @Test
    void shouldSaveAttendanceSuccessfully() {
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(attendance);

        AttendanceRequest result = attendanceService.saveAttendance(attendanceRequest);

        assertNotNull(result);
        assertEquals("class1", result.getClassId());
        assertEquals(2, result.getAttendance().size());
        verify(attendanceRepository, times(2)).save(any(Attendance.class));
    }

    // Test case for getTodayAttendanceByClass - Success
    @Test
    void shouldGetTodayAttendanceByClassSuccessfully() {
        List<Attendance> attendances = Arrays.asList(
                Attendance.builder().id("att1").studentId("student1").classId("class1").date(LocalDate.now()).status(AttendanceStatus.PRESENT).build(),
                Attendance.builder().id("att2").studentId("student2").classId("class1").date(LocalDate.now()).status(AttendanceStatus.ABSENT).build()
        );
        Student student2 = Student.builder().id("student2").fullNameWithInitials("Jane Doe").classId("class1").build();
        when(attendanceRepository.findByClassIdAndDate("class1", LocalDate.now())).thenReturn(attendances);
        when(studentRepo.findById("student1")).thenReturn(Optional.of(student));
        when(studentRepo.findById("student2")).thenReturn(Optional.of(student2));

        List<AttendanceResponse> result = attendanceService.getTodayAttendanceByClass("class1");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("student1", result.get(0).getStudentId());
        assertEquals("John Doe", result.get(0).getStudentName());
        assertEquals(AttendanceStatus.PRESENT, result.get(0).getStatus());
        verify(attendanceRepository, times(1)).findByClassIdAndDate("class1", LocalDate.now());
        verify(studentRepo, times(2)).findById(anyString());
    }

    // Test case for getTodayAttendanceByClass - Empty list
    @Test
    void shouldReturnEmptyListWhenNoAttendanceFound() {
        when(attendanceRepository.findByClassIdAndDate("class1", LocalDate.now())).thenReturn(Collections.emptyList());

        List<AttendanceResponse> result = attendanceService.getTodayAttendanceByClass("class1");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(attendanceRepository, times(1)).findByClassIdAndDate("class1", LocalDate.now());
        verify(studentRepo, never()).findById(anyString());
    }

    // Test case for getAllAttendanceByStudentId - Success
    @Test
    void shouldGetAllAttendanceByStudentIdSuccessfully() {
        List<Attendance> attendances = Arrays.asList(
                Attendance.builder().id("att1").studentId("student1").classId("class1").date(LocalDate.now()).status(AttendanceStatus.PRESENT).build()
        );
        LocalDate startDate = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        LocalDate endDate = LocalDate.now().plusDays(1);
        when(attendanceRepository.findByStudentIdAndDateBetween("student1", startDate, endDate)).thenReturn(attendances);

        List<Attendance> result = attendanceService.getAllAttendanceByStudentId("student1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("att1", result.get(0).getId());
        verify(attendanceRepository, times(1)).findByStudentIdAndDateBetween(eq("student1"), any(LocalDate.class), any(LocalDate.class));
    }

    // Test case for getAllAttendanceByStudentIdToPrincipal - Success
    @Test
    void shouldGetAllAttendanceByStudentIdToPrincipalSuccessfully() {
        List<Attendance> attendances = Arrays.asList(
                Attendance.builder().id("att1").studentId("student1").classId("class1").date(LocalDate.now()).status(AttendanceStatus.PRESENT).build(),
                Attendance.builder().id("att2").studentId("student1").classId("class1").date(LocalDate.now().minusDays(1)).status(AttendanceStatus.ABSENT).build()
        );
        when(studentRepo.findById("student1")).thenReturn(Optional.of(student));
        LocalDate startDate = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        LocalDate endDate = LocalDate.now().plusDays(1);
        when(attendanceRepository.findByStudentIdAndDateBetween("student1", startDate, endDate)).thenReturn(attendances);

        AttendanceResponse result = attendanceService.getAllAttendanceByStudentIdToPrincipal("student1");

        assertNotNull(result);
        assertEquals("student1", result.getStudentId());
        assertEquals("John Doe", result.getStudentName());
        assertEquals(2, result.getTotalDays());
        assertEquals(1, result.getTotalAttended());
        assertEquals(1, result.getTotalAbsent());
        assertEquals(50.0, result.getAttendedRate(), 0.01);
        verify(studentRepo, times(1)).findById("student1");
        verify(attendanceRepository, times(1)).findByStudentIdAndDateBetween(eq("student1"), any(LocalDate.class), any(LocalDate.class));
    }

    // Test case for getAllAttendanceByStudentIdToPrincipal - Student not found
    @Test
    void shouldThrowExceptionWhenStudentNotFoundForPrincipal() {
        when(studentRepo.findById("student1")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            attendanceService.getAllAttendanceByStudentIdToPrincipal("student1");
        });

        verify(studentRepo, times(1)).findById("student1");
        verify(attendanceRepository, never()).findByStudentIdAndDateBetween(anyString(), any(LocalDate.class), any(LocalDate.class));
    }

    // Test case for getAllAttendanceByStudentIdToParents - Success
    @Test
    void shouldGetAllAttendanceByStudentIdToParentsSuccessfully() {
        when(jwtUtil.extractUsername("token123")).thenReturn("parent_user");
        when(userRepo.findByUsername("parent_user")).thenReturn(Optional.of(user));
        when(parentRepo.findById("parent1")).thenReturn(Optional.of(parent));
        when(studentRepo.findById("student1")).thenReturn(Optional.of(student));
        List<Attendance> attendances = Arrays.asList(
                Attendance.builder().id("att1").studentId("student1").classId("class1").date(LocalDate.now()).status(AttendanceStatus.PRESENT).build(),
                Attendance.builder().id("att2").studentId("student1").classId("class1").date(LocalDate.now().minusDays(1)).status(AttendanceStatus.ABSENT).build()
        );
        LocalDate startDate = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        LocalDate endDate = LocalDate.now().plusDays(1);
        when(attendanceRepository.findByStudentIdAndDateBetween("student1", startDate, endDate)).thenReturn(attendances);

        AttendanceResponse result = attendanceService.getAllAttendanceByStudentIdToParents("token123");

        assertNotNull(result);
        assertEquals("student1", result.getStudentId());
        assertEquals("John Doe", result.getStudentName());
        assertEquals(2, result.getTotalDays());
        assertEquals(1, result.getTotalAttended());
        assertEquals(1, result.getTotalAbsent());
        assertEquals(50.0, result.getAttendedRate(), 0.01);
        verify(jwtUtil, times(1)).extractUsername("token123");
        verify(userRepo, times(1)).findByUsername("parent_user");
        verify(parentRepo, times(1)).findById("parent1");
        verify(studentRepo, times(1)).findById("student1");
        verify(attendanceRepository, times(1)).findByStudentIdAndDateBetween(eq("student1"), any(LocalDate.class), any(LocalDate.class));
    }

    // Test case for getAllAttendanceByStudentIdToParents - User not found
    @Test
    void shouldThrowExceptionWhenUserNotFoundForParents() {
        when(jwtUtil.extractUsername("token123")).thenReturn("parent_user");
        when(userRepo.findByUsername("parent_user")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            attendanceService.getAllAttendanceByStudentIdToParents("token123");
        });

        verify(jwtUtil, times(1)).extractUsername("token123");
        verify(userRepo, times(1)).findByUsername("parent_user");
        verify(parentRepo, never()).findById(anyString());
    }

    // Test case for getTodayAttendanceStatus - Success
    @Test
    void shouldGetTodayAttendanceStatusSuccessfully() {
        when(jwtUtil.extractUsername("token123")).thenReturn("parent_user");
        when(userRepo.findByUsername("parent_user")).thenReturn(Optional.of(user));
        when(parentRepo.findById("parent1")).thenReturn(Optional.of(parent));
        when(studentRepo.findById("student1")).thenReturn(Optional.of(student));
        when(attendanceRepository.findByStudentIdAndDate("student1", LocalDate.now())).thenReturn(Optional.of(attendance));
        when(classRoomRepo.findById("class1")).thenReturn(Optional.of(classRoom));
        when(teacherRepo.findById("teacher1")).thenReturn(Optional.of(teacher));
        when(userRepo.findByProfileId("teacher1")).thenReturn(user);

        Attendance result = attendanceService.getTodayAttendanceStatus("token123");

        assertNotNull(result);
        assertEquals("att1", result.getId());
        assertEquals("1234567890", result.getClassId()); // Updated to teacher contact
        verify(jwtUtil, times(1)).extractUsername("token123");
        verify(userRepo, times(1)).findByUsername("parent_user");
        verify(parentRepo, times(1)).findById("parent1");
        verify(studentRepo, times(1)).findById("student1");
        verify(attendanceRepository, times(1)).findByStudentIdAndDate("student1", LocalDate.now());
        verify(classRoomRepo, times(1)).findById("class1");
        verify(teacherRepo, times(1)).findById("teacher1");
        verify(userRepo, times(1)).findByProfileId("teacher1");
    }

    // Test case for getTodayAttendanceStatus - No attendance found
    @Test
    void shouldReturnNullWhenNoAttendanceFoundForToday() {
        when(jwtUtil.extractUsername("token123")).thenReturn("parent_user");
        when(userRepo.findByUsername("parent_user")).thenReturn(Optional.of(user));
        when(parentRepo.findById("parent1")).thenReturn(Optional.of(parent));
        when(studentRepo.findById("student1")).thenReturn(Optional.of(student));
        when(attendanceRepository.findByStudentIdAndDate("student1", LocalDate.now())).thenReturn(Optional.empty());

        Attendance result = attendanceService.getTodayAttendanceStatus("token123");

        assertNull(result);
        verify(jwtUtil, times(1)).extractUsername("token123");
        verify(userRepo, times(1)).findByUsername("parent_user");
        verify(parentRepo, times(1)).findById("parent1");
        verify(studentRepo, times(1)).findById("student1");
        verify(attendanceRepository, times(1)).findByStudentIdAndDate("student1", LocalDate.now());
        verify(classRoomRepo, never()).findById(anyString());
    }

    // Test case for getAllStudentsAllAttendanceByClassId - Success
    @Test
    void shouldGetAllStudentsAllAttendanceByClassIdSuccessfully() {
        Student student2 = Student.builder().id("student2").fullNameWithInitials("Jane Doe").classId("class1").build();
        List<Student> students = Arrays.asList(student, student2);
        List<Attendance> attendances = Arrays.asList(
                Attendance.builder().id("att1").studentId("student1").classId("class1").date(LocalDate.now()).status(AttendanceStatus.PRESENT).build(),
                Attendance.builder().id("att2").studentId("student2").classId("class1").date(LocalDate.now()).status(AttendanceStatus.ABSENT).build()
        );
        LocalDate startDate = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        LocalDate endDate = LocalDate.now().plusDays(1);
        when(studentRepo.findByClassId("class1")).thenReturn(students);
        when(attendanceRepository.findByClassIdAndDateBetween("class1", startDate, endDate)).thenReturn(attendances);
        when(attendanceRepository.findByStudentIdAndDateBetween("student1", startDate, endDate))
                .thenReturn(Arrays.asList(attendances.get(0)));
        when(attendanceRepository.findByStudentIdAndDateBetween("student2", startDate, endDate))
                .thenReturn(Arrays.asList(attendances.get(1)));

        List<AttendanceResponse> result = attendanceService.getAllStudentsAllAttendanceByClassId("class1");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("student1", result.get(0).getStudentId());
        assertEquals("John Doe", result.get(0).getStudentName());
        assertEquals(2, result.get(0).getTotalDays());
        assertEquals(1, result.get(0).getTotalAttended());
        assertEquals(50.0, result.get(0).getAttendedRate(), 0.01);
        verify(studentRepo, times(1)).findByClassId("class1");
        verify(attendanceRepository, times(1)).findByClassIdAndDateBetween(eq("class1"), any(LocalDate.class), any(LocalDate.class));
        verify(attendanceRepository, times(2)).findByStudentIdAndDateBetween(anyString(), any(LocalDate.class), any(LocalDate.class));
    }

    // Test case for getClassAttendanceBetween - Success
    @Test
    void shouldGetClassAttendanceBetweenSuccessfully() {
        List<Attendance> attendances = Arrays.asList(
                Attendance.builder().id("att1").studentId("student1").classId("class1").date(LocalDate.now()).status(AttendanceStatus.PRESENT).build()
        );
        LocalDate start = LocalDate.of(2025, 9, 1);
        LocalDate end = LocalDate.of(2025, 9, 3);
        when(attendanceRepository.findByClassIdAndDateBetween("class1", start, end)).thenReturn(attendances);

        List<Attendance> result = attendanceService.getClassAttendanceBetween("class1", start, end);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("att1", result.get(0).getId());
        verify(attendanceRepository, times(1)).findByClassIdAndDateBetween("class1", start, end);
    }

    // Test case for getStudentAttendanceBetween - Success
    @Test
    void shouldGetStudentAttendanceBetweenSuccessfully() {
        List<Attendance> attendances = Arrays.asList(
                Attendance.builder().id("att1").studentId("student1").classId("class1").date(LocalDate.now()).status(AttendanceStatus.PRESENT).build()
        );
        LocalDate start = LocalDate.of(2025, 9, 1);
        LocalDate end = LocalDate.of(2025, 9, 3);
        when(attendanceRepository.findByStudentIdAndDateBetween("student1", start, end)).thenReturn(attendances);

        List<Attendance> result = attendanceService.getStudentAttendanceBetween("student1", start, end);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("att1", result.get(0).getId());
        verify(attendanceRepository, times(1)).findByStudentIdAndDateBetween("student1", start, end);
    }

    // Test case for countSchoolDaysForClass (with date range) - Success
    @Test
    void shouldCountSchoolDaysForClassWithRangeSuccessfully() {
        LocalDate start = LocalDate.of(2025, 9, 1);
        LocalDate end = LocalDate.of(2025, 9, 5);
        when(attendanceRepository.countDistinctByClassIdAndDateBetween("class1", start, end)).thenReturn(5L);

        long result = attendanceService.countSchoolDaysForClass("class1", start, end);

        assertEquals(5L, result);
        verify(attendanceRepository, times(1)).countDistinctByClassIdAndDateBetween("class1", start, end);
    }

    // Test case for countSchoolDaysForClass (without date range) - Success
    @Test
    void shouldCountSchoolDaysForClassWithoutRangeSuccessfully() {
        when(attendanceRepository.countDistinctByClassId("class1")).thenReturn(10L);

        long result = attendanceService.countSchoolDaysForClass("class1");

        assertEquals(10L, result);
        verify(attendanceRepository, times(1)).countDistinctByClassId("class1");
    }

    // Test case for getAllAttendanceByStudentIdToPrincipal - Zero total days
    @Test
    void shouldHandleZeroTotalDaysForPrincipal() {
        when(studentRepo.findById("student1")).thenReturn(Optional.of(student));
        LocalDate startDate = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        LocalDate endDate = LocalDate.now().plusDays(1);
        when(attendanceRepository.findByStudentIdAndDateBetween("student1", startDate, endDate)).thenReturn(Collections.emptyList());

        AttendanceResponse result = attendanceService.getAllAttendanceByStudentIdToPrincipal("student1");

        assertNotNull(result);
        assertEquals("student1", result.getStudentId());
        assertEquals("John Doe", result.getStudentName());
        assertEquals(0, result.getTotalDays());
        assertEquals(0, result.getTotalAttended());
        assertEquals(0, result.getTotalAbsent());
        assertTrue(Double.isNaN(result.getAttendedRate())); // Division by zero
        verify(studentRepo, times(1)).findById("student1");
        verify(attendanceRepository, times(1)).findByStudentIdAndDateBetween(eq("student1"), any(LocalDate.class), any(LocalDate.class));
    }
}
