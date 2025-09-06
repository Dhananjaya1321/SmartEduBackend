package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.enums.Role;
import com.smartEdu.SmartEduBackend.enums.SchoolStatus;
import com.smartEdu.SmartEduBackend.repo.PrincipalRepo;
import com.smartEdu.SmartEduBackend.repo.SchoolRepo;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrincipalServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private SchoolRepo schoolRepo;

    @Mock
    private ZonalEducationOfficeRepo zonalEducationOfficeRepo;

    @Mock
    private PrincipalRepo principalRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private PrincipalService principalService;

    private PrincipalRegisterRequest principalRequest;
    private Principal principal;
    private User user;
    private School school;
    private ZonalEducationOffice zonalOffice;

    @BeforeEach
    void setUp() {
        principalRequest = PrincipalRegisterRequest.builder()
                .schoolId("SCH001")
                .fullName("John Doe")
                .moeId("MOE123")
                .nicFrontImageUrl("nic_front.jpg")
                .nicBackImageUrl("nic_back.jpg")
                .moeIdFrontImageUrl("moe_front.jpg")
                .moeIdBackImageUrl("moe_back.jpg")
                .appointmentLetterUrl("appointment.jpg")
                .nic("123456789V")
                .contact("1234567890")
                .username("johndoe")
                .password("password")
                .address("123 Main St")
                .email("john@example.com")
                .build();

        principal = Principal.builder()
                .id("PRIN001")
                .schoolId("SCH001")
                .fullName("John Doe")
                .moeId("MOE123")
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

        school = School.builder()
                .id("SCH001")
                .schoolName("Test School")
                .principal(principal)
                .build();

        zonalOffice = ZonalEducationOffice.builder()
                .id("ZEO001")
                .schoolsIds(List.of("SCH001"))
                .build();
    }

    @Test
    void testRegisterPrincipalWithUser_Success() {
        when(userRepo.findByUsername(principalRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepo.findByEmail(principalRequest.getEmail())).thenReturn(Optional.empty());
        when(principalRepo.save(any(Principal.class))).thenReturn(principal);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepo.save(any(User.class))).thenReturn(user);

        Principal result = principalService.registerPrincipalWithUser(principalRequest);

        assertNotNull(result);
        assertEquals(principal.getId(), result.getId());
        verify(principalRepo, times(1)).save(any(Principal.class));
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterPrincipalWithUser_UsernameExists() {
        when(userRepo.findByUsername(principalRequest.getUsername())).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            principalService.registerPrincipalWithUser(principalRequest);
        });

        assertEquals("Username is already exists!", exception.getMessage());
        verify(principalRepo, never()).save(any(Principal.class));
    }

    @Test
    void testRegisterPrincipalWithUser_EmailExists() {
        when(userRepo.findByUsername(principalRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepo.findByEmail(principalRequest.getEmail())).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            principalService.registerPrincipalWithUser(principalRequest);
        });

        assertEquals("Email is already exists!", exception.getMessage());
        verify(principalRepo, never()).save(any(Principal.class));
    }

    @Test
    void testUpdate_Success() {
        when(principalRepo.findById("PRIN001")).thenReturn(Optional.of(principal));
        when(schoolRepo.findById("SCH001")).thenReturn(Optional.of(school));
        when(userRepo.findByProfileId("PRIN001")).thenReturn(user);
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(principalRepo.save(any(Principal.class))).thenReturn(principal);
        when(schoolRepo.save(any(School.class))).thenReturn(school);

        PrincipalRegisterRequest updatedRequest = PrincipalRegisterRequest.builder()
                .schoolId("SCH001")
                .fullName("Jane Doe")
                .nic("987654321V")
                .contact("0987654321")
                .username("johndoe")
                .password("newpassword")
                .address("456 Main St")
                .email("jane@example.com")
                .build();

        PrincipalRegisterRequest result = principalService.update("PRIN001", updatedRequest);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getFullName());
        verify(principalRepo, times(1)).save(any(Principal.class));
        verify(schoolRepo, times(1)).save(any(School.class));
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void testUpdate_PrincipalNotFound() {
        when(principalRepo.findById("PRIN001")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            principalService.update("PRIN001", principalRequest);
        });

        assertEquals("Principal not found!", exception.getMessage());
        verify(schoolRepo, never()).save(any(School.class));
    }

    @Test
    void testDelete_Success() {
        when(principalRepo.findById("PRIN001")).thenReturn(Optional.of(principal));

        principalService.delete("PRIN001");

        verify(principalRepo, times(1)).deleteById("PRIN001");
    }

    @Test
    void testDelete_PrincipalNotFound() {
        when(principalRepo.findById("PRIN001")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            principalService.delete("PRIN001");
        });

        assertEquals("Principal not found!", exception.getMessage());
        verify(principalRepo, never()).deleteById(anyString());
    }

    @Test
    void testFindById_Success() {
        when(principalRepo.findById("PRIN001")).thenReturn(Optional.of(principal));
        when(schoolRepo.findById("SCH001")).thenReturn(Optional.of(school));

        PrincipalResponse result = principalService.findById("PRIN001");

        assertNotNull(result);
        assertEquals("Test School", result.getSchoolName());
        assertEquals("John Doe", result.getFullName());
    }

    @Test
    void testFindByIdToSchool_Success() {
        when(jwtUtil.extractUsername("token")).thenReturn("johndoe");
        when(userRepo.findByUsername("johndoe")).thenReturn(Optional.of(user));
        when(principalRepo.findById("PRIN001")).thenReturn(Optional.of(principal));
        when(schoolRepo.findById("SCH001")).thenReturn(Optional.of(school));

        PrincipalResponse result = principalService.findByIdToSchool("token");

        assertNotNull(result);
        assertEquals("Test School", result.getSchoolName());
        assertEquals("John Doe", result.getFullName());
    }

    @Test
    void testFindAll_Success() {
        when(jwtUtil.extractInstitutionId("token")).thenReturn("ZEO001");
        when(zonalEducationOfficeRepo.findById("ZEO001")).thenReturn(Optional.of(zonalOffice));
        when(principalRepo.findBySchoolId("SCH001")).thenReturn(Optional.of(principal));
        when(userRepo.findByProfileId("PRIN001")).thenReturn(user);
        when(schoolRepo.findById("SCH001")).thenReturn(Optional.of(school));

        Page<PrincipalResponse> result = principalService.findAll(0, 10, "token");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test School", result.getContent().get(0).getSchoolName());
    }

    @Test
    void testFindAll_ZonalOfficeNotFound() {
        when(jwtUtil.extractInstitutionId("token")).thenReturn("ZEO001");
        when(zonalEducationOfficeRepo.findById("ZEO001")).thenReturn(Optional.empty());

        Page<PrincipalResponse> result = principalService.findAll(0, 10, "token");

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPrincipalUserAccountDetailsByProfileId_Success() {
        when(userRepo.findByProfileId("PRIN001")).thenReturn(user);

        User result = principalService.getPrincipalUserAccountDetailsByProfileId("PRIN001");

        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
    }
}
