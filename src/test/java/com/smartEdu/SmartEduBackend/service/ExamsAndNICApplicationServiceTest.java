package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.enums.ExamsAndNICApplicationStatus;
import com.smartEdu.SmartEduBackend.repo.ExamsAndNICApplicationRepo;
import com.smartEdu.SmartEduBackend.repo.ParentRepo;
import com.smartEdu.SmartEduBackend.repo.StudentRepo;
import com.smartEdu.SmartEduBackend.repo.UserRepo;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExamsAndNICApplicationServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private StudentRepo studentRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ParentRepo parentRepo;

    @Mock
    private ExamsAndNICApplicationRepo examsAndNICApplicationRepo;

    @InjectMocks
    private ExamsAndNICApplicationService examsAndNICApplicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave_ValidApplicationAndToken_SavesApplication() {
        String token = "validToken";
        String institutionId = "school123";
        ExamsAndNICApplication application = ExamsAndNICApplication.builder()
                .type("NIC")
                .studentId("student1")
                .build();
        ExamsAndNICApplication savedApplication = ExamsAndNICApplication.builder()
                .id("app1")
                .type("NIC")
                .studentId("student1")
                .schoolId(institutionId)
                .status(ExamsAndNICApplicationStatus.PENDING)
                .build();

        when(jwtUtil.extractInstitutionId(token)).thenReturn(institutionId);
        when(examsAndNICApplicationRepo.save(any(ExamsAndNICApplication.class))).thenReturn(savedApplication);

        ExamsAndNICApplication result = examsAndNICApplicationService.save(application, token);

        assertEquals(savedApplication, result);
        assertEquals(institutionId, result.getSchoolId());
        assertEquals(ExamsAndNICApplicationStatus.PENDING, result.getStatus());
        verify(examsAndNICApplicationRepo, times(1)).save(application);
    }

    @Test
    void testFindAll_ValidTypeAndToken_ReturnsApplicationResponses() {
        String token = "validToken";
        String institutionId = "school123";
        String applicationType = "NIC";
        String studentId = "student1";
        ExamsAndNICApplication application = ExamsAndNICApplication.builder()
                .id("app1")
                .studentId(studentId)
                .schoolId(institutionId)
                .type(applicationType)
                .status(ExamsAndNICApplicationStatus.PENDING)
                .nicFrontImageUrl("front.jpg")
                .nicBackImageUrl("back.jpg")
                .build();
        Student student = Student.builder()
                .id(studentId)
                .fullNameWithInitials("John Doe")
                .registrationNumber("REG123")
                .build();
        List<ExamsAndNICApplication> applications = Arrays.asList(application);

        when(jwtUtil.extractInstitutionId(token)).thenReturn(institutionId);
        when(examsAndNICApplicationRepo.findByTypeAndSchoolId(applicationType, institutionId)).thenReturn(applications);
        when(studentRepo.findById(studentId)).thenReturn(Optional.of(student));

        List<ExamsAndNICApplicationResponse> result = examsAndNICApplicationService.findAll(applicationType, token);

        assertEquals(1, result.size());
        assertEquals("app1", result.get(0).getId());
        assertEquals(studentId, result.get(0).getStudentId());
        assertEquals("John Doe", result.get(0).getStudentName());
        assertEquals("REG123", result.get(0).getRegistrationNumber());
        assertEquals(institutionId, result.get(0).getSchoolId());
        assertEquals(applicationType, result.get(0).getType());
        assertEquals(ExamsAndNICApplicationStatus.PENDING, result.get(0).getStatus());
        assertEquals("front.jpg", result.get(0).getNicFrontImageUrl());
        assertEquals("back.jpg", result.get(0).getNicBackImageUrl());
        verify(examsAndNICApplicationRepo, times(1)).findByTypeAndSchoolId(applicationType, institutionId);
        verify(studentRepo, times(1)).findById(studentId);
    }

    @Test
    void testFindAllToParents_ValidToken_ReturnsApplicationsForStudent() {
        String token = "validToken";
        String username = "user1";
        String profileId = "parent1";
        String studentId = "student1";
        User user = User.builder().username(username).profileId(profileId).build();
        Parent parent = Parent.builder().id(profileId).studentIds(Arrays.asList(studentId)).build();
        Student student = Student.builder().id(studentId).build();
        ExamsAndNICApplicationResponse applicationResponse = ExamsAndNICApplicationResponse.builder()
                .id("app1")
                .studentId(studentId)
                .type("NIC")
                .status(ExamsAndNICApplicationStatus.PENDING)
                .build();
        List<ExamsAndNICApplicationResponse> applications = Arrays.asList(applicationResponse);

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(parentRepo.findById(profileId)).thenReturn(Optional.of(parent));
        when(studentRepo.findById(studentId)).thenReturn(Optional.of(student));
        when(examsAndNICApplicationRepo.findByStudentId(studentId)).thenReturn(applications);

        List<ExamsAndNICApplicationResponse> result = examsAndNICApplicationService.findAllToParents(token);

        assertEquals(1, result.size());
        assertEquals("app1", result.get(0).getId());
        assertEquals(studentId, result.get(0).getStudentId());
        assertEquals("NIC", result.get(0).getType());
        assertEquals(ExamsAndNICApplicationStatus.PENDING, result.get(0).getStatus());
        verify(userRepo, times(1)).findByUsername(username);
        verify(parentRepo, times(1)).findById(profileId);
        verify(studentRepo, times(1)).findById(studentId);
        verify(examsAndNICApplicationRepo, times(1)).findByStudentId(studentId);
    }
}
