package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.CustomUserDetails;
import com.smartEdu.SmartEduBackend.entity.User;
import com.smartEdu.SmartEduBackend.enums.Role;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepo userRepo;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private EmailUtil emailUtil;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    private User user;
    private CustomUserDetails userDetails;
    private String token = "testToken";

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id("U001")
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .name("Test User")
                .role(Role.SCHOOL_ADMIN)
                .contact("1234567890")
                .nic("123456789V")
                .address("123 Main St")
                .active(true)
                .institutionID("INST001")
                .build();

        userDetails = new CustomUserDetails(user);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    @Test
    void saveUser_SuccessWithProvidedPassword() {
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(jwtUtil.extractInstitutionId(token)).thenReturn("INST001");
        when(userRepo.save(any(User.class))).thenReturn(user);

        User inputUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .name("Test User")
                .role(Role.SCHOOL_ADMIN)
                .build();

        User result = userService.save(inputUser, token);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("INST001", result.getInstitutionID());
        verify(userRepo, times(1)).save(any(User.class));
        verify(emailUtil, times(1)).sendEmail(eq("test@example.com"), anyString(), anyString());
    }

    @Test
    void saveUser_SuccessWithGeneratedPassword() {
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(PasswordGeneratorUtil.generate()).thenReturn("generatedPass");
        when(passwordEncoder.encode("generatedPass")).thenReturn("encodedPassword");
        when(jwtUtil.extractInstitutionId(token)).thenReturn("INST001");
        when(userRepo.save(any(User.class))).thenReturn(user);

        User inputUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("")
                .name("Test User")
                .role(Role.SCHOOL_ADMIN)
                .build();

        User result = userService.save(inputUser, token);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepo, times(1)).save(any(User.class));
        verify(emailUtil, times(1)).sendEmail(eq("test@example.com"), anyString(), contains("generatedPass"));
    }

    @Test
    void saveUser_UsernameExists_ThrowsException() {
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.save(user, token));

        assertEquals("Username is already exists!", exception.getMessage());
        verify(userRepo, never()).save(any(User.class));
        verify(emailUtil, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void saveUser_EmailExists_ThrowsException() {
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.save(user, token));

        assertEquals("Email is already exists!", exception.getMessage());
        verify(userRepo, never()).save(any(User.class));
        verify(emailUtil, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void updateUser_Success() {
        when(userRepo.findById("U001")).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenReturn(user);

        User updatedUser = User.builder()
                .username("updateduser")
                .email("updated@example.com")
                .name("Updated User")
                .role(Role.SCHOOL_ADMIN)
                .build();

        User result = userService.update("U001", updatedUser);

        assertNotNull(result);
        assertEquals("updateduser", result.getUsername());
        assertEquals("INST001", result.getInstitutionID());
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_Unauthorized_ThrowsException() {
        user.setRole(Role.SCHOOL_EMPLOYEE);
        userDetails = new CustomUserDetails(user);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        User updatedUser = User.builder()
                .username("updateduser")
                .email("updated@example.com")
                .role(Role.SCHOOL_ADMIN)
                .build();

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.update("U001", updatedUser));

        assertEquals("Unauthorized to update users", exception.getMessage());
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void updateUser_UserNotFound_ThrowsException() {
        when(userRepo.findById("U001")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.update("U001", user));

        assertEquals("User is not exists!", exception.getMessage());
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void deleteUser_Success() {
        when(userRepo.findById("U001")).thenReturn(Optional.of(user));

        userService.delete("U001");

        verify(userRepo, times(1)).deleteById("U001");
    }

    @Test
    void deleteUser_Unauthorized_ThrowsException() {
        user.setRole(Role.SCHOOL_EMPLOYEE);
        userDetails = new CustomUserDetails(user);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepo.findById("U001")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.delete("U001"));

        assertEquals("Unauthorized to delete user", exception.getMessage());
        verify(userRepo, never()).deleteById(anyString());
    }

    @Test
    void deleteUser_UserNotFound_ThrowsException() {
        when(userRepo.findById("U001")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.delete("U001"));

        assertEquals("User is not exists!", exception.getMessage());
        verify(userRepo, never()).deleteById(anyString());
    }

    @Test
    void findById_Success() {
        when(userRepo.findById("U001")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById("U001");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepo, times(1)).findById("U001");
    }

    @Test
    void findById_NotFound() {
        when(userRepo.findById("U001")).thenReturn(Optional.empty());

        Optional<User> result = userService.findById("U001");

        assertFalse(result.isPresent());
        verify(userRepo, times(1)).findById("U001");
    }

    @Test
    void findAllByRole_Success() {
        when(jwtUtil.extractInstitutionId(token)).thenReturn("INST001");
        when(userRepo.findAllByRoleStartingWithAndInstitutionID("SCHOOL_", "INST001"))
                .thenReturn(Arrays.asList(user));

        List<User> result = userService.findAllByRole(token);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        verify(userRepo, times(1)).findAllByRoleStartingWithAndInstitutionID("SCHOOL_", "INST001");
    }

    @Test
    void findAllByRole_NoUsers_ReturnsEmptyList() {
        when(jwtUtil.extractInstitutionId(token)).thenReturn("INST001");
        when(userRepo.findAllByRoleStartingWithAndInstitutionID("SCHOOL_", "INST001"))
                .thenReturn(Collections.emptyList());

        List<User> result = userService.findAllByRole(token);

        assertTrue(result.isEmpty());
        verify(userRepo, times(1)).findAllByRoleStartingWithAndInstitutionID("SCHOOL_", "INST001");
    }

    @Test
    void checkEmailAndSendOTP_Success() {
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        String otp = userService.checkEmailAndSendOTP("test@example.com");

        assertNotNull(otp);
        assertEquals(6, otp.length());
        verify(emailUtil, times(1)).sendEmail(eq("test@example.com"), anyString(), contains(otp));
    }

    @Test
    void checkEmailAndSendOTP_EmailNotFound_ThrowsException() {
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.checkEmailAndSendOTP("test@example.com"));

        assertEquals("Incorrect email", exception.getMessage());
        verify(emailUtil, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void updatePassword_Success() {
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepo.save(any(User.class))).thenReturn(user);

        String result = userService.updatePassword("test@example.com", "newPassword");

        assertEquals("Password updated for test@example.com", result);
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void updatePassword_UserNotFound_ThrowsException() {
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.updatePassword("test@example.com", "newPassword"));

        assertEquals("User not found with given email", exception.getMessage());
        verify(userRepo, never()).save(any(User.class));
    }
}
