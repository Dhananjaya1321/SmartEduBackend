package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.enums.Role;
import com.smartEdu.SmartEduBackend.repo.ProvincialEducationOfficeRepo;
import com.smartEdu.SmartEduBackend.repo.UserRepo;
import com.smartEdu.SmartEduBackend.repo.ZonalEducationOfficeRepo;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZMOEServiceTest {

    @Mock
    private ZonalEducationOfficeRepo zmoeRepo;

    @Mock
    private ProvincialEducationOfficeRepo provincialEducationOfficeRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private EmailUtil emailUtil;

    @InjectMocks
    private ZMOEService zmoeService;

    private ZonalEducationOfficeRequest request;
    private ZonalEducationOffice zmoe;
    private User user;
    private ProvincialEducationOffice province;

    @BeforeEach
    void setUp() {
        request = new ZonalEducationOfficeRequest();
        request.setProvince("Western");
        request.setDistrict("Colombo");
        request.setZonal("Colombo North");
        request.setOfficeAddress("123 Zonal St");
        request.setFullName("John Doe");
        request.setName("John Doe");
        request.setContact("1234567890");
        request.setNic("123456789V");
        request.setUsername("johndoe");
        request.setEmail("john@example.com");
        request.setAddress("456 Main St");

        zmoe = ZonalEducationOffice.builder()
                .id("Z001")
                .province("Western")
                .district("Colombo")
                .zonal("Colombo North")
                .officeAddress("123 Zonal St")
                .fullName("John Doe")
                .build();

        user = User.builder()
                .id("U001")
                .username("johndoe")
                .password("encodedPassword")
                .role(Role.ZMOE_ADMIN)
                .name("John Doe")
                .email("john@example.com")
                .nic("123456789V")
                .contact("1234567890")
                .address("456 Main St")
                .active(true)
                .institutionID("Z001")
                .build();

        province = ProvincialEducationOffice.builder()
                .id("P001")
                .province("Western")
                .zonalOffices(new ArrayList<>())
                .build();
    }

    @Test
    void createZonalEducationOfficeWithUser_Success() {
        when(userRepo.findByUsername("johndoe")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(zmoeRepo.save(any(ZonalEducationOffice.class))).thenReturn(zmoe);
        when(provincialEducationOfficeRepo.findByProvince("Western")).thenReturn(province);
        when(provincialEducationOfficeRepo.save(any(ProvincialEducationOffice.class))).thenReturn(province);
        when(PasswordGeneratorUtil.generate()).thenReturn("generatedPass");
        when(passwordEncoder.encode("generatedPass")).thenReturn("encodedPassword");
        when(userRepo.save(any(User.class))).thenReturn(user);

        ZonalEducationOffice result = zmoeService.createZonalEducationOfficeWithUser(request);

        assertNotNull(result);
        assertEquals("Western", result.getProvince());
        assertEquals("Colombo North", result.getZonal());
        verify(zmoeRepo, times(1)).save(any(ZonalEducationOffice.class));
        verify(provincialEducationOfficeRepo, times(1)).save(any(ProvincialEducationOffice.class));
        verify(userRepo, times(1)).save(any(User.class));
        verify(emailUtil, times(1)).sendEmail(eq("john@example.com"), anyString(), contains("generatedPass"));
    }

    @Test
    void createZonalEducationOfficeWithUser_UsernameExists_ThrowsException() {
        when(userRepo.findByUsername("johndoe")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                zmoeService.createZonalEducationOfficeWithUser(request));

        assertEquals("Username is already exists!", exception.getMessage());
        verify(zmoeRepo, never()).save(any(ZonalEducationOffice.class));
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void createZonalEducationOfficeWithUser_EmailExists_ThrowsException() {
        when(userRepo.findByUsername("johndoe")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                zmoeService.createZonalEducationOfficeWithUser(request));

        assertEquals("Email is already exists!", exception.getMessage());
        verify(zmoeRepo, never()).save(any(ZonalEducationOffice.class));
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void createNewAdminForZonalEducationOffice_Success() {
        when(userRepo.findByUsername("johndoe")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(PasswordGeneratorUtil.generate()).thenReturn("generatedPass");
        when(passwordEncoder.encode("generatedPass")).thenReturn("encodedPassword");
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(zmoeRepo.findById("Z001")).thenReturn(Optional.of(zmoe));
        when(zmoeRepo.save(any(ZonalEducationOffice.class))).thenReturn(zmoe);

        User result = zmoeService.createNewAdminForZonalEducationOffice("Z001", request);

        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        assertEquals("Z001", result.getInstitutionID());
        verify(userRepo, times(1)).deleteByInstitutionIDAndRole("Z001", Role.ZMOE_ADMIN);
        verify(userRepo, times(1)).save(any(User.class));
        verify(zmoeRepo, times(1)).save(any(ZonalEducationOffice.class));
        verify(emailUtil, times(1)).sendEmail(eq("john@example.com"), anyString(), contains("generatedPass"));
    }

    @Test
    void createNewAdminForZonalEducationOffice_UsernameExists_ThrowsException() {
        when(userRepo.findByUsername("johndoe")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                zmoeService.createNewAdminForZonalEducationOffice("Z001", request));

        assertEquals("Username is already exists!", exception.getMessage());
        verify(userRepo, never()).save(any(User.class));
        verify(zmoeRepo, never()).save(any(ZonalEducationOffice.class));
    }

    @Test
    void createNewAdminForZonalEducationOffice_EmailExists_ThrowsException() {
        when(userRepo.findByUsername("johndoe")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                zmoeService.createNewAdminForZonalEducationOffice("Z001", request));

        assertEquals("Email is already exists!", exception.getMessage());
        verify(userRepo, never()).save(any(User.class));
        verify(zmoeRepo, never()).save(any(ZonalEducationOffice.class));
    }

    @Test
    void findAll_Success() {
        PageRequest pageable = PageRequest.of(0, 10);
        List<User> users = Arrays.asList(user);
        when(userRepo.findAllByRole(pageable, Role.ZMOE_ADMIN)).thenReturn(users);
        when(userRepo.countByRole(Role.ZMOE_ADMIN)).thenReturn(1L);
        when(zmoeRepo.findById("Z001")).thenReturn(Optional.of(zmoe));

        Page<ZonalEducationOfficeAdminResponse> result = zmoeService.findAll(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("johndoe", result.getContent().get(0).getUsername());
        assertEquals("Colombo North", result.getContent().get(0).getZonal());
        verify(userRepo, times(1)).findAllByRole(pageable, Role.ZMOE_ADMIN);
        verify(zmoeRepo, times(1)).findById("Z001");
    }

    @Test
    void findAll_NoUsers_ReturnsEmptyPage() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(userRepo.findAllByRole(pageable, Role.ZMOE_ADMIN)).thenReturn(Collections.emptyList());
        when(userRepo.countByRole(Role.ZMOE_ADMIN)).thenReturn(0L);

        Page<ZonalEducationOfficeAdminResponse> result = zmoeService.findAll(0, 10);

        assertTrue(result.isEmpty());
        verify(userRepo, times(1)).findAllByRole(pageable, Role.ZMOE_ADMIN);
        verify(zmoeRepo, never()).findById(anyString());
    }

    @Test
    void findAll_ZonalOfficeNotFound_SkipsUser() {
        PageRequest pageable = PageRequest.of(0, 10);
        List<User> users = Arrays.asList(user);
        when(userRepo.findAllByRole(pageable, Role.ZMOE_ADMIN)).thenReturn(users);
        when(userRepo.countByRole(Role.ZMOE_ADMIN)).thenReturn(1L);
        when(zmoeRepo.findById("Z001")).thenReturn(Optional.empty());

        Page<ZonalEducationOfficeAdminResponse> result = zmoeService.findAll(0, 10);

        assertTrue(result.isEmpty());
        verify(userRepo, times(1)).findAllByRole(pageable, Role.ZMOE_ADMIN);
        verify(zmoeRepo, times(1)).findById("Z001");
    }

    @Test
    void updateZMOE_Success() {
        ZonalEducationOffice updatedOffice = ZonalEducationOffice.builder()
                .id("Z001")
                .province("Western")
                .district("Colombo")
                .zonal("Colombo North")
                .officeAddress("Updated Address")
                .fullName("John Doe")
                .build();

        when(zmoeRepo.findById("Z001")).thenReturn(Optional.of(zmoe));
        when(zmoeRepo.save(any(ZonalEducationOffice.class))).thenReturn(updatedOffice);

        ZonalEducationOffice result = zmoeService.updateZMOE("Z001", updatedOffice);

        assertNotNull(result);
        assertEquals("Updated Address", result.getOfficeAddress());
        verify(zmoeRepo, times(1)).findById("Z001");
        verify(zmoeRepo, times(1)).save(any(ZonalEducationOffice.class));
    }

    @Test
    void updateZMOE_OfficeNotFound_ThrowsException() {
        ZonalEducationOffice updatedOffice = ZonalEducationOffice.builder()
                .id("Z001")
                .officeAddress("Updated Address")
                .build();

        when(zmoeRepo.findById("Z001")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                zmoeService.updateZMOE("Z001", updatedOffice));

        assertEquals("Office not found!", exception.getMessage());
        verify(zmoeRepo, times(1)).findById("Z001");
        verify(zmoeRepo, never()).save(any(ZonalEducationOffice.class));
    }
}
