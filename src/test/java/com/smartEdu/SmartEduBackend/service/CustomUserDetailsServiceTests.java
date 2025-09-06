package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.User;
import com.smartEdu.SmartEduBackend.entity.CustomUserDetails;
import com.smartEdu.SmartEduBackend.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTests {

    @Mock
    private UserRepo userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("test_user")
                .password("password")
                .profileId("profile1")
                .build();
    }

    @Test
    void shouldLoadUserByUsernameSuccessfully() {
        when(userRepository.findByUsername("test_user")).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("test_user");

        assertNotNull(result);
        assertTrue(result instanceof CustomUserDetails);
        assertEquals("test_user", result.getUsername());
        verify(userRepository, times(1)).findByUsername("test_user");
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findByUsername("test_user")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("test_user");
        }, "User not found");

        verify(userRepository, times(1)).findByUsername("test_user");
    }
}
