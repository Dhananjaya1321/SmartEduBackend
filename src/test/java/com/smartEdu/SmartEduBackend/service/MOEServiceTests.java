package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.MinistryOfEducationOffice;
import com.smartEdu.SmartEduBackend.entity.MinistryOfEducationOfficeRequest;
import com.smartEdu.SmartEduBackend.entity.User;
import com.smartEdu.SmartEduBackend.enums.Role;
import com.smartEdu.SmartEduBackend.repo.MinistryEducationOfficeRepo;
import com.smartEdu.SmartEduBackend.repo.UserRepo;
import com.smartEdu.SmartEduBackend.util.EmailUtil;
import com.smartEdu.SmartEduBackend.util.PasswordGeneratorUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MOEServiceTests {

    @Mock
    private UserRepo userRepo;

    @Mock
    private MinistryEducationOfficeRepo ministryEducationOfficeRepo;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private EmailUtil emailUtil;

    @InjectMocks
    private MOEService moeService;

    private MinistryOfEducationOfficeRequest request;
    private MinistryOfEducationOffice office;
    private User user;

    @BeforeEach
    void setUp() {
        request = new MinistryOfEducationOfficeRequest();
        request.setOfficeAddress("123 MOE Street");
        request.setName("MOE Office");
        request.setNic("123456789V");
        request.setContact("1234567890");
        request.setUsername("moe_admin");
        request.setAddress("456 Admin Road");
        request.setEmail("moe@admin.com");

        office = MinistryOfEducationOffice.builder()
                .id("moe1")
                .officeAddress("123 MOE Street")
                .name("MOE Office")
                .build();

        user = User.builder()
                .username("moe_admin")
                .password("encoded_password")
                .role(Role.MOE_ADMIN)
                .email("moe@admin.com")
                .nic("123456789V")
                .contact("1234567890")
                .address("456 Admin Road")
                .active(true)
                .institutionID("moe1")
                .build();
    }

    @Test
    void shouldCreateMinistryOfEducationOfficeWithUserSuccessfully() throws Exception {
        when(userRepo.findByUsername("moe_admin")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("moe@admin.com")).thenReturn(Optional.empty());
        when(ministryEducationOfficeRepo.save(any(MinistryOfEducationOffice.class))).thenReturn(office);
        when(PasswordGeneratorUtil.generate()).thenReturn("randomPassword");
        when(passwordEncoder.encode("randomPassword")).thenReturn("encoded_password");
        when(userRepo.save(any(User.class))).thenReturn(user);
        doNothing().when(emailUtil).sendEmail(eq("moe@admin.com"), anyString(), anyString());

        MinistryOfEducationOffice result = moeService.createMinistryOfEducationOfficeWithUser(request);

        assertNotNull(result);
        assertEquals("moe1", result.getId());
        assertEquals("MOE Office", result.getName());
        verify(userRepo, times(1)).findByUsername("moe_admin");
        verify(userRepo, times(1)).findByEmail("moe@admin.com");
        verify(ministryEducationOfficeRepo, times(1)).save(any(MinistryOfEducationOffice.class));
        verify(passwordEncoder, times(1)).encode("randomPassword");
        verify(userRepo, times(1)).save(any(User.class));
        verify(emailUtil, times(1)).sendEmail(eq("moe@admin.com"), anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        when(userRepo.findByUsername("moe_admin")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> {
            moeService.createMinistryOfEducationOfficeWithUser(request);
        }, "Username is already exists!");

        verify(userRepo, times(1)).findByUsername("moe_admin");
        verify(userRepo, never()).findByEmail(anyString());
        verify(ministryEducationOfficeRepo, never()).save(any(MinistryOfEducationOffice.class));
        verify(userRepo, never()).save(any(User.class));
        verify(emailUtil, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepo.findByUsername("moe_admin")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("moe@admin.com")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> {
            moeService.createMinistryOfEducationOfficeWithUser(request);
        }, "Email is already exists!");

        verify(userRepo, times(1)).findByUsername("moe_admin");
        verify(userRepo, times(1)).findByEmail("moe@admin.com");
        verify(ministryEducationOfficeRepo, never()).save(any(MinistryOfEducationOffice.class));
        verify(userRepo, never()).save(any(User.class));
        verify(emailUtil, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void shouldFindAllMinistryOfEducationOfficesSuccessfully() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MinistryOfEducationOffice> page = new PageImpl<>(Arrays.asList(office));
        when(ministryEducationOfficeRepo.findAll(pageable)).thenReturn(page);

        Page<MinistryOfEducationOffice> result = moeService.findAll(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("moe1", result.getContent().get(0).getId());
        verify(ministryEducationOfficeRepo, times(1)).findAll(pageable);
    }

    @Test
    void shouldReturnEmptyPageWhenNoMinistryOfficesFound() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MinistryOfEducationOffice> emptyPage = new PageImpl<>(Collections.emptyList());
        when(ministryEducationOfficeRepo.findAll(pageable)).thenReturn(emptyPage);

        Page<MinistryOfEducationOffice> result = moeService.findAll(0, 10);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        verify(ministryEducationOfficeRepo, times(1)).findAll(pageable);
    }
}
