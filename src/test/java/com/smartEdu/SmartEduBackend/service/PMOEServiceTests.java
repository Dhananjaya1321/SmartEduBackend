package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.enums.Role;
import com.smartEdu.SmartEduBackend.repo.MinistryEducationOfficeRepo;
import com.smartEdu.SmartEduBackend.repo.ProvincialEducationOfficeRepo;
import com.smartEdu.SmartEduBackend.repo.UserRepo;
import com.smartEdu.SmartEduBackend.util.EmailUtil;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PMOEServiceTests {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ProvincialEducationOfficeRepo provincialEducationOfficeRepo;

    @Mock
    private MinistryEducationOfficeRepo ministryEducationOfficeRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private EmailUtil emailUtil;

    @InjectMocks
    private PMOEService pmoeService;

    private ProvincialEducationOfficeRequest request;
    private ProvincialEducationOffice office;
    private MinistryOfEducationOffice ministry;
    private User user;

    @BeforeEach
    void setUp() {
        request = new ProvincialEducationOfficeRequest();
        request.setProvince("Western");
        request.setOfficeAddress("123 Province St");
        request.setName("PMOE Office");
        request.setNic("123456789V");
        request.setContact("1234567890");
        request.setUsername("pmoe_admin");
        request.setAddress("456 Admin Rd");
        request.setEmail("pmoe@admin.com");

        office = ProvincialEducationOffice.builder()
                .id("pmoe1")
                .province("Western")
                .officeAddress("123 Province St")
                .name("PMOE Office")
                .build();

        ministry = MinistryOfEducationOffice.builder()
                .id("moe1")
                .provincialOffices(new ArrayList<>())
                .build();

        user = User.builder()
                .id("user1")
                .username("pmoe_admin")
                .password("encoded_password")
                .role(Role.PMOE_ADMIN)
                .name("PMOE Office")
                .email("pmoe@admin.com")
                .nic("123456789V")
                .contact("1234567890")
                .address("456 Admin Rd")
                .active(true)
                .institutionID("pmoe1")
                .build();
    }

    @Test
    void shouldCreateProvincialEducationOfficeWithUserSuccessfully() throws Exception {
        when(userRepo.findByUsername("pmoe_admin")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("pmoe@admin.com")).thenReturn(Optional.empty());
        when(ministryEducationOfficeRepo.findAll()).thenReturn(Arrays.asList(ministry));
        when(provincialEducationOfficeRepo.save(any(ProvincialEducationOffice.class))).thenReturn(office);
        when(PasswordGeneratorUtil.generate()).thenReturn("randomPassword");
        when(passwordEncoder.encode("randomPassword")).thenReturn("encoded_password");
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(ministryEducationOfficeRepo.save(any(MinistryOfEducationOffice.class))).thenReturn(ministry);
        doNothing().when(emailUtil).sendEmail(eq("pmoe@admin.com"), anyString(), anyString());

        ProvincialEducationOffice result = pmoeService.createProvincialEducationOfficeWithUser(request);

        assertNotNull(result);
        assertEquals("pmoe1", result.getId());
        assertEquals("Western", result.getProvince());
        verify(userRepo, times(1)).findByUsername("pmoe_admin");
        verify(userRepo, times(1)).findByEmail("pmoe@admin.com");
        verify(ministryEducationOfficeRepo, times(1)).findAll();
        verify(provincialEducationOfficeRepo, times(1)).save(any(ProvincialEducationOffice.class));
        verify(passwordEncoder, times(1)).encode("randomPassword");
        verify(userRepo, times(1)).save(any(User.class));
        verify(ministryEducationOfficeRepo, times(1)).save(ministry);
        verify(emailUtil, times(1)).sendEmail(eq("pmoe@admin.com"), anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExistsOnCreateOffice() {
        when(userRepo.findByUsername("pmoe_admin")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> {
            pmoeService.createProvincialEducationOfficeWithUser(request);
        }, "Username is already exists!");

        verify(userRepo, times(1)).findByUsername("pmoe_admin");
        verify(userRepo, never()).findByEmail(anyString());
        verify(ministryEducationOfficeRepo, never()).findAll();
        verify(provincialEducationOfficeRepo, never()).save(any(ProvincialEducationOffice.class));
        verify(userRepo, never()).save(any(User.class));
        verify(emailUtil, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExistsOnCreateOffice() {
        when(userRepo.findByUsername("pmoe_admin")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("pmoe@admin.com")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> {
            pmoeService.createProvincialEducationOfficeWithUser(request);
        }, "Email is already exists!");

        verify(userRepo, times(1)).findByUsername("pmoe_admin");
        verify(userRepo, times(1)).findByEmail("pmoe@admin.com");
        verify(ministryEducationOfficeRepo, never()).findAll();
        verify(provincialEducationOfficeRepo, never()).save(any(ProvincialEducationOffice.class));
        verify(userRepo, never()).save(any(User.class));
        verify(emailUtil, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void shouldCreateNewAdminForProvincialEducationOfficeSuccessfully() {
        when(userRepo.findByUsername("pmoe_admin")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("pmoe@admin.com")).thenReturn(Optional.empty());
        when(PasswordGeneratorUtil.generate()).thenReturn("randomPassword");
        when(passwordEncoder.encode("randomPassword")).thenReturn("encoded_password");
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(provincialEducationOfficeRepo.findById("pmoe1")).thenReturn(Optional.of(office));
        when(provincialEducationOfficeRepo.save(any(ProvincialEducationOffice.class))).thenReturn(office);
        doNothing().when(userRepo).deleteByInstitutionIDAndRole("pmoe1", Role.PMOE_ADMIN);
        doNothing().when(emailUtil).sendEmail(eq("pmoe@admin.com"), anyString(), anyString());

        User result = pmoeService.createNewAdminForProvincialEducationOffice("pmoe1", request);

        assertNotNull(result);
        assertEquals("user1", result.getId());
        assertEquals("pmoe_admin", result.getUsername());
        verify(userRepo, times(1)).findByUsername("pmoe_admin");
        verify(userRepo, times(1)).findByEmail("pmoe@admin.com");
        verify(userRepo, times(1)).deleteByInstitutionIDAndRole("pmoe1", Role.PMOE_ADMIN);
        verify(passwordEncoder, times(1)).encode("randomPassword");
        verify(userRepo, times(1)).save(any(User.class));
        verify(provincialEducationOfficeRepo, times(1)).findById("pmoe1");
        verify(provincialEducationOfficeRepo, times(1)).save(any(ProvincialEducationOffice.class));
        verify(emailUtil, times(1)).sendEmail(eq("pmoe@admin.com"), anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExistsOnCreateNewAdmin() {
        when(userRepo.findByUsername("pmoe_admin")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> {
            pmoeService.createNewAdminForProvincialEducationOffice("pmoe1", request);
        }, "Username is already exists!");

        verify(userRepo, times(1)).findByUsername("pmoe_admin");
        verify(userRepo, never()).findByEmail(anyString());
        verify(userRepo, never()).deleteByInstitutionIDAndRole(anyString(), any(Role.class));
        verify(userRepo, never()).save(any(User.class));
        verify(provincialEducationOfficeRepo, never()).findById(anyString());
        verify(emailUtil, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExistsOnCreateNewAdmin() {
        when(userRepo.findByUsername("pmoe_admin")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("pmoe@admin.com")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> {
            pmoeService.createNewAdminForProvincialEducationOffice("pmoe1", request);
        }, "Email is already exists!");

        verify(userRepo, times(1)).findByUsername("pmoe_admin");
        verify(userRepo, times(1)).findByEmail("pmoe@admin.com");
        verify(userRepo, never()).deleteByInstitutionIDAndRole(anyString(), any(Role.class));
        verify(userRepo, never()).save(any(User.class));
        verify(provincialEducationOfficeRepo, never()).findById(anyString());
        verify(emailUtil, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenOfficeNotFoundOnCreateNewAdmin() {
        when(userRepo.findByUsername("pmoe_admin")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("pmoe@admin.com")).thenReturn(Optional.empty());
        when(provincialEducationOfficeRepo.findById("pmoe1")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            pmoeService.createNewAdminForProvincialEducationOffice("pmoe1", request);
        });

        verify(userRepo, times(1)).findByUsername("pmoe_admin");
        verify(userRepo, times(1)).findByEmail("pmoe@admin.com");
        verify(userRepo, times(1)).deleteByInstitutionIDAndRole("pmoe1", Role.PMOE_ADMIN);
        verify(provincialEducationOfficeRepo, times(1)).findById("pmoe1");
        verify(userRepo, never()).save(any(User.class));
        verify(emailUtil, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void shouldUpdatePMOESuccessfully() {
        ProvincialEducationOffice updatedOffice = ProvincialEducationOffice.builder()
                .id("pmoe1")
                .province("Western")
                .officeAddress("Updated Address")
                .name("Updated PMOE Office")
                .build();
        when(provincialEducationOfficeRepo.findById("pmoe1")).thenReturn(Optional.of(office));
        when(provincialEducationOfficeRepo.save(any(ProvincialEducationOffice.class))).thenReturn(updatedOffice);

        ProvincialEducationOffice result = pmoeService.updatePMOE("pmoe1", updatedOffice);

        assertNotNull(result);
        assertEquals("pmoe1", result.getId());
        assertEquals("Updated Address", result.getOfficeAddress());
        verify(provincialEducationOfficeRepo, times(1)).findById("pmoe1");
        verify(provincialEducationOfficeRepo, times(1)).save(any(ProvincialEducationOffice.class));
    }

    @Test
    void shouldThrowExceptionWhenOfficeNotFoundOnUpdate() {
        when(provincialEducationOfficeRepo.findById("pmoe1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            pmoeService.updatePMOE("pmoe1", office);
        }, "Office not found!");

        verify(provincialEducationOfficeRepo, times(1)).findById("pmoe1");
        verify(provincialEducationOfficeRepo, never()).save(any(ProvincialEducationOffice.class));
    }

    @Test
    void shouldFindAllProvincialEducationOfficesSuccessfully() {
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = Arrays.asList(user);
        when(userRepo.findAllByRole(pageable, Role.PMOE_ADMIN)).thenReturn(users);
        when(userRepo.countByRole(Role.PMOE_ADMIN)).thenReturn(1L);
        when(provincialEducationOfficeRepo.findById("pmoe1")).thenReturn(Optional.of(office));

        Page<ProvincialEducationOfficeAdminResponse> result = pmoeService.findAll(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("pmoe_admin", result.getContent().get(0).getUsername());
        assertEquals("Western", result.getContent().get(0).getProvince());
        verify(userRepo, times(1)).findAllByRole(pageable, Role.PMOE_ADMIN);
        verify(userRepo, times(1)).countByRole(Role.PMOE_ADMIN);
        verify(provincialEducationOfficeRepo, times(1)).findById("pmoe1");
    }

    @Test
    void shouldReturnEmptyPageWhenNoProvincialOfficesFound() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepo.findAllByRole(pageable, Role.PMOE_ADMIN)).thenReturn(Collections.emptyList());
        when(userRepo.countByRole(Role.PMOE_ADMIN)).thenReturn(0L);

        Page<ProvincialEducationOfficeAdminResponse> result = pmoeService.findAll(0, 10);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        verify(userRepo, times(1)).findAllByRole(pageable, Role.PMOE_ADMIN);
        verify(userRepo, times(1)).countByRole(Role.PMOE_ADMIN);
        verify(provincialEducationOfficeRepo, never()).findById(anyString());
    }

    @Test
    void shouldGetLoggedInProvinceSuccessfully() {
        when(jwtUtil.extractInstitutionId("token123")).thenReturn("pmoe1");
        when(provincialEducationOfficeRepo.findById("pmoe1")).thenReturn(Optional.of(office));

        String result = pmoeService.getLoggedInProvince("token123");

        assertEquals("Western", result);
        verify(jwtUtil, times(1)).extractInstitutionId("token123");
        verify(provincialEducationOfficeRepo, times(1)).findById("pmoe1");
    }

    @Test
    void shouldThrowExceptionWhenOfficeNotFoundForLoggedInProvince() {
        when(jwtUtil.extractInstitutionId("token123")).thenReturn("pmoe1");
        when(provincialEducationOfficeRepo.findById("pmoe1")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            pmoeService.getLoggedInProvince("token123");
        });

        verify(jwtUtil, times(1)).extractInstitutionId("token123");
        verify(provincialEducationOfficeRepo, times(1)).findById("pmoe1");
    }
}
