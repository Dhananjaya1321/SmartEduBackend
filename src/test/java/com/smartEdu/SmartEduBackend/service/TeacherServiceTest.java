package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.enums.Role;
import com.smartEdu.SmartEduBackend.repo.SchoolRepo;
import com.smartEdu.SmartEduBackend.repo.TeacherRepo;
import com.smartEdu.SmartEduBackend.repo.UserRepo;
import com.smartEdu.SmartEduBackend.repo.ZonalEducationOfficeRepo;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private TeacherRepo teacherRepo;

    @Mock
    private SchoolRepo schoolRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ZonalEducationOfficeRepo zonalEducationOfficeRepo;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private TeacherService teacherService;

    private TeacherRegisterRequest request;
    private Teacher teacher;
    private User user;
    private School school;
    private ZonalEducationOffice zonalEducationOffice;

    @BeforeEach
    void setUp() {
        request = TeacherRegisterRequest.builder()
                .fullName("John Doe")
                .schoolId("SCH001")
                .nic("123456789V")
                .contact("1234567890")
                .username("johndoe")
                .password("password123")
                .address("123 Main St")
                .email("john@example.com")
                .build();

        teacher = Teacher.builder()
                .id("T001")
                .fullName("John Doe")
                .schoolId("SCH001")
                .build();

        user = User.builder()
                .id("U001")
                .nic("123456789V")
                .contact("1234567890")
                .username("johndoe")
                .password("encodedPassword")
                .address("123 Main St")
                .email("john@example.com")
                .role(Role.TEACHER)
                .name("John Doe")
                .active(true)
                .profileId("T001")
                .institutionID("SCH001")
                .build();

        school = School.builder()
                .id("SCH001")
                .schoolName("Test School")
                .build();

        zonalEducationOffice = ZonalEducationOffice.builder()
                .id("Z001")
                .schoolsIds(Arrays.asList("SCH001"))
                .build();
    }

    @Test
    void registerTeacherWithUser_Success() {
        when(userRepo.findByUsername("johndoe")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(teacherRepo.save(any(Teacher.class))).thenReturn(teacher);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepo.save(any(User.class))).thenReturn(user);

        Teacher result = teacherService.registerTeacherWithUser(request);

        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
        assertEquals("SCH001", result.getSchoolId());
        verify(teacherRepo, times(1)).save(any(Teacher.class));
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void registerTeacherWithUser_UsernameExists_ThrowsException() {
        when(userRepo.findByUsername("johndoe")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                teacherService.registerTeacherWithUser(request));

        assertEquals("Username is already exists!", exception.getMessage());
        verify(teacherRepo, never()).save(any(Teacher.class));
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void registerTeacherWithUser_EmailExists_ThrowsException() {
        when(userRepo.findByUsername("johndoe")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                teacherService.registerTeacherWithUser(request));

        assertEquals("Email is already exists!", exception.getMessage());
        verify(teacherRepo, never()).save(any(Teacher.class));
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void updateTeacher_Success() {
        when(teacherRepo.findById("T001")).thenReturn(Optional.of(teacher));
        when(userRepo.findByProfileId("T001")).thenReturn(user);
        when(teacherRepo.save(any(Teacher.class))).thenReturn(teacher);
        when(userRepo.save(any(User.class))).thenReturn(user);

        TeacherRegisterRequest updatedRequest = TeacherRegisterRequest.builder()
                .fullName("Jane Doe")
                .schoolId("SCH001")
                .nic("987654321V")
                .contact("0987654321")
                .username("janedoe")
                .password("password123")
                .address("456 Main St")
                .email("jane@example.com")
                .build();

        TeacherRegisterRequest result = teacherService.update("T001", updatedRequest);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getFullName());
        assertEquals("SCH001", result.getSchoolId());
        verify(teacherRepo, times(1)).save(any(Teacher.class));
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void updateTeacher_TeacherNotFound_ThrowsException() {
        when(teacherRepo.findById("T001")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                teacherService.update("T001", request));

        assertEquals("Teacher not found!", exception.getMessage());
        verify(teacherRepo, never()).save(any(Teacher.class));
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void deleteTeacher_Success() {
        when(teacherRepo.findById("T001")).thenReturn(Optional.of(teacher));
        when(userRepo.findByProfileId("T001")).thenReturn(user);

        teacherService.delete("T001");

        verify(teacherRepo, times(1)).deleteById("T001");
        verify(userRepo, times(1)).deleteById("U001");
    }

    @Test
    void deleteTeacher_TeacherNotFound_ThrowsException() {
        when(teacherRepo.findById("T001")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                teacherService.delete("T001"));

        assertEquals("Teacher not found!", exception.getMessage());
        verify(teacherRepo, never()).deleteById(anyString());
        verify(userRepo, never()).deleteById(anyString());
    }

    @Test
    void findById_Success() {
        when(teacherRepo.findById("T001")).thenReturn(Optional.of(teacher));

        Optional<Teacher> result = teacherService.findById("T001");

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getFullName());
        verify(teacherRepo, times(1)).findById("T001");
    }

    @Test
    void findById_NotFound() {
        when(teacherRepo.findById("T001")).thenReturn(Optional.empty());

        Optional<Teacher> result = teacherService.findById("T001");

        assertFalse(result.isPresent());
        verify(teacherRepo, times(1)).findById("T001");
    }

    @Test
    void findAllForZonalOffice_Success() {
        when(jwtUtil.extractInstitutionId("token")).thenReturn("Z001");
        when(zonalEducationOfficeRepo.findById("Z001")).thenReturn(Optional.of(zonalEducationOffice));
        when(teacherRepo.findAllBySchoolId("SCH001")).thenReturn(Arrays.asList(teacher));
        when(schoolRepo.findById("SCH001")).thenReturn(Optional.of(school));
        when(userRepo.findByProfileId("T001")).thenReturn(user);

        Page<TeacherResponse> result = teacherService.findAllForZonalOffice(0, 10, "token");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("John Doe", result.getContent().get(0).getFullName());
        assertEquals("Test School", result.getContent().get(0).getSchoolName());
        verify(zonalEducationOfficeRepo, times(1)).findById("Z001");
        verify(teacherRepo, times(1)).findAllBySchoolId("SCH001");
    }

    @Test
    void findAllForZonalOffice_ZonalOfficeNotFound_ReturnsEmpty() {
        when(jwtUtil.extractInstitutionId("token")).thenReturn("Z001");
        when(zonalEducationOfficeRepo.findById("Z001")).thenReturn(Optional.empty());

        Page<TeacherResponse> result = teacherService.findAllForZonalOffice(0, 10, "token");

        assertTrue(result.isEmpty());
        verify(zonalEducationOfficeRepo, times(1)).findById("Z001");
        verify(teacherRepo, never()).findAllBySchoolId(anyString());
    }

    @Test
    void findAllForZonalOffice_SchoolNotFound_ReturnsEmpty() {
        when(jwtUtil.extractInstitutionId("token")).thenReturn("Z001");
        when(zonalEducationOfficeRepo.findById("Z001")).thenReturn(Optional.of(zonalEducationOffice));
        when(schoolRepo.findById("SCH001")).thenReturn(Optional.empty());

        Page<TeacherResponse> result = teacherService.findAllForZonalOffice(0, 10, "token");

        assertTrue(result.isEmpty());
        verify(zonalEducationOfficeRepo, times(1)).findById("Z001");
        verify(schoolRepo, times(1)).findById("SCH001");
    }

    @Test
    void findAllForZonalOffice_EmptyPage() {
        when(jwtUtil.extractInstitutionId("token")).thenReturn("Z001");
        when(zonalEducationOfficeRepo.findById("Z001")).thenReturn(Optional.of(zonalEducationOffice));
        when(teacherRepo.findAllBySchoolId("SCH001")).thenReturn(Collections.emptyList());
        when(schoolRepo.findById("SCH001")).thenReturn(Optional.of(school));

        Page<TeacherResponse> result = teacherService.findAllForZonalOffice(0, 10, "token");

        assertTrue(result.isEmpty());
        verify(zonalEducationOfficeRepo, times(1)).findById("Z001");
        verify(teacherRepo, times(1)).findAllBySchoolId("SCH001");
    }

    @Test
    void findAllForSchool_Success() {
        when(jwtUtil.extractInstitutionId("token")).thenReturn("SCH001");
        when(schoolRepo.findById("SCH001")).thenReturn(Optional.of(school));
        when(teacherRepo.findAllBySchoolId("SCH001")).thenReturn(Arrays.asList(teacher));
        when(userRepo.findByProfileId("T001")).thenReturn(user);

        Page<TeacherResponse> result = teacherService.findAllForSchool(0, 10, "token");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("John Doe", result.getContent().get(0).getFullName());
        assertEquals("Test School", result.getContent().get(0).getSchoolName());
        verify(schoolRepo, times(1)).findById("SCH001");
        verify(teacherRepo, times(1)).findAllBySchoolId("SCH001");
    }

    @Test
    void findAllForSchool_SchoolNotFound_ReturnsEmpty() {
        when(jwtUtil.extractInstitutionId("token")).thenReturn("SCH001");
        when(schoolRepo.findById("SCH001")).thenReturn(Optional.empty());

        Page<TeacherResponse> result = teacherService.findAllForSchool(0, 10, "token");

        assertTrue(result.isEmpty());
        verify(schoolRepo, times(1)).findById("SCH001");
        verify(teacherRepo, never()).findAllBySchoolId(anyString());
    }

    @Test
    void findAllForSchool_EmptyPage() {
        when(jwtUtil.extractInstitutionId("token")).thenReturn("SCH001");
        when(schoolRepo.findById("SCH001")).thenReturn(Optional.of(school));
        when(teacherRepo.findAllBySchoolId("SCH001")).thenReturn(Collections.emptyList());

        Page<TeacherResponse> result = teacherService.findAllForSchool(0, 10, "token");

        assertTrue(result.isEmpty());
        verify(schoolRepo, times(1)).findById("SCH001");
        verify(teacherRepo, times(1)).findAllBySchoolId("SCH001");
    }
}
