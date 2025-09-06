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

import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTests {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepo userRepo;

    @Mock
    private StudentRepo studentRepo;

    @Mock
    private MinistryEducationOfficeRepo ministryEducationOfficeRepo;

    @Mock
    private ZonalEducationOfficeRepo zonalEducationOfficeRepo;

    @Mock
    private ProvincialEducationOfficeRepo provincialEducationOfficeRepo;

    @Mock
    private ParentRepo parentRepo;

    @Mock
    private TeacherRepo teacherRepo;

    @InjectMocks
    private DashboardService dashboardService;

    private ZonalEducationOffice zonalEducationOffice;
    private ProvincialEducationOffice provincialEducationOffice;
    private MinistryOfEducationOffice ministryOfEducationOffice;

    @BeforeEach
    void setUp() {
        zonalEducationOffice = ZonalEducationOffice.builder()
                .id("zonal1")
                .schoolsIds(Arrays.asList("school1", "school2"))
                .build();

        provincialEducationOffice = ProvincialEducationOffice.builder()
                .id("prov1")
                .zonalOffices(Arrays.asList(zonalEducationOffice))
                .build();

        ministryOfEducationOffice = MinistryOfEducationOffice.builder()
                .id("moe1")
                .provincialOffices(Arrays.asList(provincialEducationOffice))
                .build();
    }

    @Test
    void shouldGetSchoolDashboardDetailsSuccessfully() {
        when(jwtUtil.extractInstitutionId("token123")).thenReturn("school1");
        when(userRepo.countAllByInstitutionIDAndRole("school1", Role.SCHOOL_EMPLOYEE)).thenReturn(5);
        when(userRepo.countAllByInstitutionIDAndRole("school1", Role.TEACHER)).thenReturn(10);
        when(studentRepo.countBySchoolId("school1")).thenReturn(100);

        Dashboard result = dashboardService.getSchoolDashboardDetails("token123");

        assertNotNull(result);
        assertEquals(100, result.getStudentsCount());
        assertEquals(100, result.getParentsCount());
        assertEquals(10, result.getTeachersCount());
        assertEquals(6, result.getStaffUsersCount());
        assertEquals(0, result.getSchoolsCount());
        assertEquals(0, result.getZonalCount());
        assertEquals(0, result.getProvinceCount());
        verify(jwtUtil, times(1)).extractInstitutionId("token123");
        verify(userRepo, times(1)).countAllByInstitutionIDAndRole("school1", Role.SCHOOL_EMPLOYEE);
        verify(userRepo, times(1)).countAllByInstitutionIDAndRole("school1", Role.TEACHER);
        verify(studentRepo, times(1)).countBySchoolId("school1");
    }

    @Test
    void shouldGetZonalDashboardDetailsSuccessfully() {
        when(jwtUtil.extractInstitutionId("token123")).thenReturn("zonal1");
        when(userRepo.countAllByInstitutionIDAndRole("zonal1", Role.ZMOE_EMPLOYEE)).thenReturn(3);
        when(zonalEducationOfficeRepo.findById("zonal1")).thenReturn(Optional.of(zonalEducationOffice));
        when(userRepo.countAllByInstitutionIDAndRole("school1", Role.TEACHER)).thenReturn(10);
        when(userRepo.countAllByInstitutionIDAndRole("school2", Role.TEACHER)).thenReturn(15);
        when(studentRepo.countBySchoolId("school1")).thenReturn(100);
        when(studentRepo.countBySchoolId("school2")).thenReturn(200);

        Dashboard result = dashboardService.getZonalDashboardDetails("token123");

        assertNotNull(result);
        assertEquals(300, result.getStudentsCount());
        assertEquals(300, result.getParentsCount());
        assertEquals(25, result.getTeachersCount());
        assertEquals(2, result.getPrincipalsCount());
        assertEquals(2, result.getSchoolsCount());
        assertEquals(4, result.getStaffUsersCount());
        assertEquals(0, result.getZonalCount());
        assertEquals(0, result.getProvinceCount());
        verify(jwtUtil, times(1)).extractInstitutionId("token123");
        verify(userRepo, times(1)).countAllByInstitutionIDAndRole("zonal1", Role.ZMOE_EMPLOYEE);
        verify(zonalEducationOfficeRepo, times(1)).findById("zonal1");
        verify(userRepo, times(2)).countAllByInstitutionIDAndRole(anyString(), eq(Role.TEACHER));
        verify(studentRepo, times(2)).countBySchoolId(anyString());
    }

    @Test
    void shouldThrowExceptionWhenZonalOfficeNotFound() {
        when(jwtUtil.extractInstitutionId("token123")).thenReturn("zonal1");
        when(zonalEducationOfficeRepo.findById("zonal1")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            dashboardService.getZonalDashboardDetails("token123");
        });

        verify(jwtUtil, times(1)).extractInstitutionId("token123");
        verify(zonalEducationOfficeRepo, times(1)).findById("zonal1");
        verify(userRepo, never()).countAllByInstitutionIDAndRole(anyString(), any(Role.class));
        verify(studentRepo, never()).countBySchoolId(anyString());
    }

    @Test
    void shouldGetProvinceDashboardDetailsSuccessfully() {
        when(jwtUtil.extractInstitutionId("token123")).thenReturn("prov1");
        when(userRepo.countAllByInstitutionIDAndRole("prov1", Role.PMOE_EMPLOYEE)).thenReturn(4);
        when(provincialEducationOfficeRepo.findById("prov1")).thenReturn(Optional.of(provincialEducationOffice));
        when(zonalEducationOfficeRepo.findById("zonal1")).thenReturn(Optional.of(zonalEducationOffice));
        when(userRepo.countAllByInstitutionIDAndRole("school1", Role.TEACHER)).thenReturn(10);
        when(userRepo.countAllByInstitutionIDAndRole("school2", Role.TEACHER)).thenReturn(15);
        when(studentRepo.countBySchoolId("school1")).thenReturn(100);
        when(studentRepo.countBySchoolId("school2")).thenReturn(200);

        Dashboard result = dashboardService.getProvinceDashboardDetails("token123");

        assertNotNull(result);
        assertEquals(300, result.getStudentsCount());
        assertEquals(300, result.getParentsCount());
        assertEquals(25, result.getTeachersCount());
        assertEquals(2, result.getPrincipalsCount());
        assertEquals(2, result.getSchoolsCount());
        assertEquals(1, result.getZonalCount());
        assertEquals(5, result.getStaffUsersCount());
        assertEquals(0, result.getProvinceCount());
        verify(jwtUtil, times(1)).extractInstitutionId("token123");
        verify(userRepo, times(1)).countAllByInstitutionIDAndRole("prov1", Role.PMOE_EMPLOYEE);
        verify(provincialEducationOfficeRepo, times(1)).findById("prov1");
        verify(zonalEducationOfficeRepo, times(1)).findById("zonal1");
        verify(userRepo, times(2)).countAllByInstitutionIDAndRole(anyString(), eq(Role.TEACHER));
        verify(studentRepo, times(2)).countBySchoolId(anyString());
    }

    @Test
    void shouldThrowExceptionWhenProvincialOfficeNotFound() {
        when(jwtUtil.extractInstitutionId("token123")).thenReturn("prov1");
        when(provincialEducationOfficeRepo.findById("prov1")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            dashboardService.getProvinceDashboardDetails("token123");
        });

        verify(jwtUtil, times(1)).extractInstitutionId("token123");
        verify(provincialEducationOfficeRepo, times(1)).findById("prov1");
        verify(zonalEducationOfficeRepo, never()).findById(anyString());
        verify(userRepo, never()).countAllByInstitutionIDAndRole(anyString(), any(Role.class));
        verify(studentRepo, never()).countBySchoolId(anyString());
    }

    @Test
    void shouldGetMOEDashboardDetailsSuccessfully() {
        when(jwtUtil.extractInstitutionId("token123")).thenReturn("moe1");
        when(userRepo.countAllByInstitutionIDAndRole("moe1", Role.MOE_EMPLOYEE)).thenReturn(5);
        when(ministryEducationOfficeRepo.findById("moe1")).thenReturn(Optional.of(ministryOfEducationOffice));
        when(provincialEducationOfficeRepo.findById("prov1")).thenReturn(Optional.of(provincialEducationOffice));
        when(zonalEducationOfficeRepo.findById("zonal1")).thenReturn(Optional.of(zonalEducationOffice));
        when(userRepo.countAllByInstitutionIDAndRole("school1", Role.TEACHER)).thenReturn(10);
        when(userRepo.countAllByInstitutionIDAndRole("school2", Role.TEACHER)).thenReturn(15);
        when(studentRepo.countBySchoolId("school1")).thenReturn(100);
        when(studentRepo.countBySchoolId("school2")).thenReturn(200);

        Dashboard result = dashboardService.getDetailsToMOEDashboard("token123");

        assertNotNull(result);
        assertEquals(300, result.getStudentsCount());
        assertEquals(300, result.getParentsCount());
        assertEquals(25, result.getTeachersCount());
        assertEquals(2, result.getPrincipalsCount());
        assertEquals(2, result.getSchoolsCount());
        assertEquals(1, result.getZonalCount());
        assertEquals(1, result.getProvinceCount());
        assertEquals(6, result.getStaffUsersCount());
        verify(jwtUtil, times(1)).extractInstitutionId("token123");
        verify(userRepo, times(1)).countAllByInstitutionIDAndRole("moe1", Role.MOE_EMPLOYEE);
        verify(ministryEducationOfficeRepo, times(1)).findById("moe1");
        verify(provincialEducationOfficeRepo, times(1)).findById("prov1");
        verify(zonalEducationOfficeRepo, times(1)).findById("zonal1");
        verify(userRepo, times(2)).countAllByInstitutionIDAndRole(anyString(), eq(Role.TEACHER));
        verify(studentRepo, times(2)).countBySchoolId(anyString());
    }

    @Test
    void shouldThrowExceptionWhenMOENotFound() {
        when(jwtUtil.extractInstitutionId("token123")).thenReturn("moe1");
        when(ministryEducationOfficeRepo.findById("moe1")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            dashboardService.getDetailsToMOEDashboard("token123");
        });

        verify(jwtUtil, times(1)).extractInstitutionId("token123");
        verify(ministryEducationOfficeRepo, times(1)).findById("moe1");
        verify(provincialEducationOfficeRepo, never()).findById(anyString());
        verify(zonalEducationOfficeRepo, never()).findById(anyString());
        verify(userRepo, never()).countAllByInstitutionIDAndRole(anyString(), any(Role.class));
        verify(studentRepo, never()).countBySchoolId(anyString());
    }

    @Test
    void shouldHandleEmptyZonalOfficesInProvinceDashboard() {
        ProvincialEducationOffice emptyProvincialOffice = ProvincialEducationOffice.builder()
                .id("prov1")
                .zonalOffices(Collections.emptyList())
                .build();
        when(jwtUtil.extractInstitutionId("token123")).thenReturn("prov1");
        when(userRepo.countAllByInstitutionIDAndRole("prov1", Role.PMOE_EMPLOYEE)).thenReturn(4);
        when(provincialEducationOfficeRepo.findById("prov1")).thenReturn(Optional.of(emptyProvincialOffice));

        Dashboard result = dashboardService.getProvinceDashboardDetails("token123");

        assertNotNull(result);
        assertEquals(0, result.getStudentsCount());
        assertEquals(0, result.getParentsCount());
        assertEquals(0, result.getTeachersCount());
        assertEquals(0, result.getPrincipalsCount());
        assertEquals(0, result.getSchoolsCount());
        assertEquals(0, result.getZonalCount());
        assertEquals(5, result.getStaffUsersCount());
        verify(jwtUtil, times(1)).extractInstitutionId("token123");
        verify(userRepo, times(1)).countAllByInstitutionIDAndRole("prov1", Role.PMOE_EMPLOYEE);
        verify(provincialEducationOfficeRepo, times(1)).findById("prov1");
        verify(zonalEducationOfficeRepo, never()).findById(anyString());
        verify(userRepo, never()).countAllByInstitutionIDAndRole(anyString(), eq(Role.TEACHER));
        verify(studentRepo, never()).countBySchoolId(anyString());
    }

    @Test
    void shouldHandleEmptyProvincialOfficesInMOEDashboard() {
        MinistryOfEducationOffice emptyMinistry = MinistryOfEducationOffice.builder()
                .id("moe1")
                .provincialOffices(Collections.emptyList())
                .build();
        when(jwtUtil.extractInstitutionId("token123")).thenReturn("moe1");
        when(userRepo.countAllByInstitutionIDAndRole("moe1", Role.MOE_EMPLOYEE)).thenReturn(5);
        when(ministryEducationOfficeRepo.findById("moe1")).thenReturn(Optional.of(emptyMinistry));

        Dashboard result = dashboardService.getDetailsToMOEDashboard("token123");

        assertNotNull(result);
        assertEquals(0, result.getStudentsCount());
        assertEquals(0, result.getParentsCount());
        assertEquals(0, result.getTeachersCount());
        assertEquals(0, result.getPrincipalsCount());
        assertEquals(0, result.getSchoolsCount());
        assertEquals(0, result.getZonalCount());
        assertEquals(0, result.getProvinceCount());
        assertEquals(6, result.getStaffUsersCount());
        verify(jwtUtil, times(1)).extractInstitutionId("token123");
        verify(userRepo, times(1)).countAllByInstitutionIDAndRole("moe1", Role.MOE_EMPLOYEE);
        verify(ministryEducationOfficeRepo, times(1)).findById("moe1");
        verify(provincialEducationOfficeRepo, never()).findById(anyString());
        verify(zonalEducationOfficeRepo, never()).findById(anyString());
        verify(userRepo, never()).countAllByInstitutionIDAndRole(anyString(), eq(Role.TEACHER));
        verify(studentRepo, never()).countBySchoolId(anyString());
    }
}
