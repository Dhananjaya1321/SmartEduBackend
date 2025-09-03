package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.enums.Role;
import com.smartEdu.SmartEduBackend.repo.ParentRepo;
import com.smartEdu.SmartEduBackend.repo.StudentRepo;
import com.smartEdu.SmartEduBackend.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ParentServiceTest {

    @Mock
    private ParentRepo parentRepo;

    @Mock
    private StudentRepo studentRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ParentService parentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterParent_ValidRequest_SavesParentAndUser() {
        String studentId = "student1";
        ParentRegisterRequest request = new ParentRegisterRequest(
                "John Doe", "123456789V", "1234567890", "john@example.com", "REG123", "johndoe", "password123"
        );
        User user = User.builder()
                .username("johndoe")
                .password("encodedPassword")
                .nic("123456789V")
                .contact("1234567890")
                .email("john@example.com")
                .role(Role.PARENT)
                .name("John Doe")
                .institutionID("PARENT")
                .active(true)
                .build();
        Student student = Student.builder().id(studentId).registrationNumber("REG123").build();
        Parent parent = Parent.builder().id("parent1").fullName("John Doe").studentIds(Arrays.asList(studentId)).build();

        when(userRepo.findByUsername("johndoe")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(studentRepo.findByRegistrationNumber("REG123")).thenReturn(Optional.of(student));
        when(parentRepo.save(any(Parent.class))).thenReturn(parent);

        Parent result = parentService.registerParent(request);

        assertEquals(parent, result);
        assertEquals("John Doe", result.getFullName());
        assertEquals(Arrays.asList(studentId), result.getStudentIds());
        verify(userRepo, times(2)).save(any(User.class));
        verify(parentRepo, times(1)).save(any(Parent.class));
    }

    @Test
    void testRegisterParent_ExistingUsername_ThrowsRuntimeException() {
        ParentRegisterRequest request = new ParentRegisterRequest(
                "John Doe", "123456789V", "1234567890", "john@example.com", "REG123", "johndoe", "password123"
        );
        User existingUser = User.builder().username("johndoe").build();

        when(userRepo.findByUsername("johndoe")).thenReturn(Optional.of(existingUser));

        assertThrows(RuntimeException.class, () -> parentService.registerParent(request));
        verify(userRepo, never()).save(any(User.class));
        verify(parentRepo, never()).save(any(Parent.class));
    }

    @Test
    void testRegisterParent_ExistingEmail_ThrowsRuntimeException() {
        ParentRegisterRequest request = new ParentRegisterRequest(
                "John Doe", "123456789V", "1234567890", "john@example.com", "REG123", "johndoe", "password123"
        );
        User existingUser = User.builder().email("john@example.com").build();

        when(userRepo.findByUsername("johndoe")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("john@example.com")).thenReturn(Optional.of(existingUser));

        assertThrows(RuntimeException.class, () -> parentService.registerParent(request));
        verify(userRepo, never()).save(any(User.class));
        verify(parentRepo, never()).save(any(Parent.class));
    }

    @Test
    void testRegisterParent_NonExistingStudent_ThrowsRuntimeException() {
        ParentRegisterRequest request = new ParentRegisterRequest(
                "John Doe", "123456789V", "1234567890", "john@example.com", "REG123", "johndoe", "password123"
        );

        when(userRepo.findByUsername("johndoe")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(studentRepo.findByRegistrationNumber("REG123")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> parentService.registerParent(request));
        verify(userRepo, never()).save(any(User.class));
        verify(parentRepo, never()).save(any(Parent.class));
    }

    @Test
    void testUpdate_ValidId_UpdatesParent() {
        String id = "parent1";
        Parent existingParent = Parent.builder().id(id).fullName("John Doe").build();
        Parent updatedParent = Parent.builder().fullName("Jane Doe").studentIds(Arrays.asList("student1")).build();
        Parent savedParent = Parent.builder().id(id).fullName("Jane Doe").studentIds(Arrays.asList("student1")).build();

        when(parentRepo.findById(id)).thenReturn(Optional.of(existingParent));
        when(parentRepo.save(any(Parent.class))).thenReturn(savedParent);

        Parent result = parentService.update(id, updatedParent);

        assertEquals(savedParent, result);
        assertEquals(id, result.getId());
        assertEquals("Jane Doe", result.getFullName());
        verify(parentRepo, times(1)).save(any(Parent.class));
    }

    @Test
    void testUpdate_NonExistingId_ThrowsRuntimeException() {
        String id = "nonExisting";
        Parent updatedParent = Parent.builder().fullName("Jane Doe").build();

        when(parentRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> parentService.update(id, updatedParent));
        verify(parentRepo, never()).save(any(Parent.class));
    }

    @Test
    void testVerifyAndLinkStudent_ValidDetails_LinksStudent() {
        String registrationNumber = "REG123";
        String parentName = "Jane Doe";
        String contact = "1234567890";
        String parentId = "parent1";
        String studentId = "student1";
        Student student = Student.builder()
                .id(studentId)
                .registrationNumber(registrationNumber)
                .motherName("Jane Doe")
                .motherContact("1234567890")
                .build();
        Parent parent = Parent.builder().id(parentId).studentIds(new ArrayList<>()).build();
        Parent updatedParent = Parent.builder().id(parentId).studentIds(Arrays.asList(studentId)).build();

        when(studentRepo.findByRegistrationNumber(registrationNumber)).thenReturn(Optional.of(student));
        when(parentRepo.findById(parentId)).thenReturn(Optional.of(parent));
        when(parentRepo.save(any(Parent.class))).thenReturn(updatedParent);

        Student result = parentService.verifyAndLinkStudent(registrationNumber, parentName, contact, parentId);

        assertEquals(student, result);
        assertEquals(Arrays.asList(studentId), updatedParent.getStudentIds());
        verify(parentRepo, times(1)).save(any(Parent.class));
    }

    @Test
    void testVerifyAndLinkStudent_InvalidRegistrationNumber_ThrowsRuntimeException() {
        String registrationNumber = "REG123";
        String parentName = "Jane Doe";
        String contact = "1234567890";
        String parentId = "parent1";

        when(studentRepo.findByRegistrationNumber(registrationNumber)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> parentService.verifyAndLinkStudent(registrationNumber, parentName, contact, parentId));
        verify(parentRepo, never()).save(any(Parent.class));
    }

    @Test
    void testVerifyAndLinkStudent_NonMatchingParentDetails_ThrowsRuntimeException() {
        String registrationNumber = "REG123";
        String parentName = "Jane Doe";
        String contact = "1234567890";
        String parentId = "parent1";
        Student student = Student.builder()
                .id("student1")
                .registrationNumber(registrationNumber)
                .motherName("Mary Doe")
                .motherContact("0987654321")
                .fatherName("John Smith")
                .fatherContact("0987654321")
                .build();

        when(studentRepo.findByRegistrationNumber(registrationNumber)).thenReturn(Optional.of(student));

        assertThrows(RuntimeException.class, () -> parentService.verifyAndLinkStudent(registrationNumber, parentName, contact, parentId));
        verify(parentRepo, never()).save(any(Parent.class));
    }

    @Test
    void testVerifyAndLinkStudent_NonExistingParent_ThrowsRuntimeException() {
        String registrationNumber = "REG123";
        String parentName = "Jane Doe";
        String contact = "1234567890";
        String parentId = "parent1";
        Student student = Student.builder()
                .id("student1")
                .registrationNumber(registrationNumber)
                .motherName("Jane Doe")
                .motherContact("1234567890")
                .build();

        when(studentRepo.findByRegistrationNumber(registrationNumber)).thenReturn(Optional.of(student));
        when(parentRepo.findById(parentId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> parentService.verifyAndLinkStudent(registrationNumber, parentName, contact, parentId));
        verify(parentRepo, never()).save(any(Parent.class));
    }

    @Test
    void testDelete_ValidId_DeletesParent() {
        String id = "parent1";
        Parent parent = Parent.builder().id(id).build();

        when(parentRepo.findById(id)).thenReturn(Optional.of(parent));

        parentService.delete(id);

        verify(parentRepo, times(1)).deleteById(id);
    }

    @Test
    void testDelete_NonExistingId_ThrowsRuntimeException() {
        String id = "nonExisting";

        when(parentRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> parentService.delete(id));
        verify(parentRepo, never()).deleteById(anyString());
    }

    @Test
    void testFindById_ValidId_ReturnsParent() {
        String id = "parent1";
        Parent parent = Parent.builder().id(id).fullName("John Doe").build();

        when(parentRepo.findById(id)).thenReturn(Optional.of(parent));

        Optional<Parent> result = parentService.findById(id);

        assertTrue(result.isPresent());
        assertEquals(parent, result.get());
        verify(parentRepo, times(2)).findById(id); // Called in service and assertion
    }

    @Test
    void testFindById_NonExistingId_ThrowsRuntimeException() {
        String id = "nonExisting";

        when(parentRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> parentService.findById(id));
        verify(parentRepo, times(1)).findById(id);
    }

    @Test
    void testFindAll_ValidPageRequest_ReturnsParents() {
        int page = 0;
        int size = 10;
        Parent parent = Parent.builder().id("parent1").fullName("John Doe").build();
        Page<Parent> parentPage = new PageImpl<>(Arrays.asList(parent));

        when(parentRepo.findAll(PageRequest.of(page, size))).thenReturn(parentPage);

        List<Parent> result = parentService.findAll(page, size);

        assertEquals(1, result.size());
        assertEquals("parent1", result.get(0).getId());
        verify(parentRepo, times(1)).findAll(PageRequest.of(page, size));
    }
}
