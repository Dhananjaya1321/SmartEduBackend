package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.Achievements;
import com.smartEdu.SmartEduBackend.entity.Student;
import com.smartEdu.SmartEduBackend.enums.AchievementsCategory;
import com.smartEdu.SmartEduBackend.enums.AchievementsLevels;
import com.smartEdu.SmartEduBackend.enums.AchievementsPlace;
import com.smartEdu.SmartEduBackend.repo.AchievementRepo;
import com.smartEdu.SmartEduBackend.repo.StudentRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AchievementServiceTests {

    @Mock
    private StudentRepo studentRepo;

    @Mock
    private AchievementRepo achievementRepo;

    @InjectMocks
    private AchievementService achievementService;

    private Student student;
    private Achievements achievement;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId("student1");
        student.setAchievements(new ArrayList<>());

        achievement = Achievements.builder()
                .id("ach1")
                .studentId("student1")
                .name("Math Olympiad")
                .description("Won first place in national competition")
                .level(AchievementsLevels.NATIONAL_LEVEL)
                .place(AchievementsPlace.FIRST)
                .category(AchievementsCategory.SPORT)
                .build();
    }

    // Test case for save - Success
    @Test
    void shouldSaveAchievementSuccessfully() {
        when(studentRepo.findById("student1")).thenReturn(Optional.of(student));
        when(achievementRepo.save(any(Achievements.class))).thenReturn(achievement);
        when(studentRepo.save(any(Student.class))).thenReturn(student);

        Achievements result = achievementService.save("student1", achievement);

        assertNotNull(result);
        assertEquals("Math Olympiad", result.getName());
        assertEquals("student1", result.getStudentId());
        verify(studentRepo, times(1)).findById("student1");
        verify(achievementRepo, times(1)).save(any(Achievements.class));
        verify(studentRepo, times(1)).save(student);
        assertTrue(student.getAchievements().contains(achievement));
    }

    // Test case for save - Student not exists
    @Test
    void shouldThrowExceptionWhenStudentNotExistsOnSave() {
        when(studentRepo.findById("student1")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            achievementService.save("student1", achievement);
        });

        assertEquals("Student is not exists!", exception.getMessage());
        verify(studentRepo, times(1)).findById("student1");
        verify(achievementRepo, never()).save(any(Achievements.class));
        verify(studentRepo, never()).save(any(Student.class));
    }

    // Test case for update - Success
    @Test
    void shouldUpdateAchievementSuccessfully() {
        Achievements updatedAchievement = Achievements.builder()
                .id("ach1")
                .studentId("student1")
                .name("Science Fair")
                .description("Updated description")
                .level(AchievementsLevels.NATIONAL_LEVEL)
                .place(AchievementsPlace.FIRST)
                .category(AchievementsCategory.SPORT)
                .build();

        when(achievementRepo.findById("ach1")).thenReturn(Optional.of(achievement));
        when(achievementRepo.save(any(Achievements.class))).thenReturn(updatedAchievement);

        Achievements result = achievementService.update("ach1", updatedAchievement);

        assertNotNull(result);
        assertEquals("Science Fair", result.getName());
        assertEquals("ach1", result.getId());
        verify(achievementRepo, times(1)).findById("ach1");
        verify(achievementRepo, times(1)).save(updatedAchievement);
    }

    // Test case for update - Achievement not exists
    @Test
    void shouldThrowExceptionWhenAchievementNotExistsOnUpdate() {
        when(achievementRepo.findById("ach1")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            achievementService.update("ach1", achievement);
        });

        assertEquals("Achievement is not exists!", exception.getMessage());
        verify(achievementRepo, times(1)).findById("ach1");
        verify(achievementRepo, never()).save(any(Achievements.class));
    }

    // Test case for delete - Success
    @Test
    void shouldDeleteAchievementSuccessfully() {
        when(achievementRepo.findById("ach1")).thenReturn(Optional.of(achievement));
        when(studentRepo.findById("student1")).thenReturn(Optional.of(student));
        when(studentRepo.save(any(Student.class))).thenReturn(student);
        doNothing().when(achievementRepo).deleteById("ach1");

        achievementService.delete("ach1");

        verify(achievementRepo, times(1)).findById("ach1");
        verify(studentRepo, times(1)).findById("student1");
        verify(studentRepo, times(1)).save(student);
        verify(achievementRepo, times(1)).deleteById("ach1");
        assertFalse(student.getAchievements().contains(achievement));
    }

    // Test case for delete - Achievement not exists
    @Test
    void shouldThrowExceptionWhenAchievementNotExistsOnDelete() {
        when(achievementRepo.findById("ach1")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            achievementService.delete("ach1");
        });

        assertEquals("Achievement is not exists!", exception.getMessage());
        verify(achievementRepo, times(1)).findById("ach1");
        verify(studentRepo, never()).findById(anyString());
        verify(studentRepo, never()).save(any(Student.class));
        verify(achievementRepo, never()).deleteById(anyString());
    }

    // Test case for getAchievementsByStudentId - Success
    @Test
    void shouldGetAchievementsByStudentIdSuccessfully() {
        List<Achievements> achievements = new ArrayList<>();
        achievements.add(achievement);
        student.setAchievements(achievements);

        when(studentRepo.findById("student1")).thenReturn(Optional.of(student));

        List<Achievements> result = achievementService.getAchievementsByStudentId("student1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Math Olympiad", result.get(0).getName());
        verify(studentRepo, times(1)).findById("student1");
    }

    // Test case for getAchievementsByStudentId - Student not exists
    @Test
    void shouldThrowExceptionWhenStudentNotExistsOnGetAchievements() {
        when(studentRepo.findById("student1")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            achievementService.getAchievementsByStudentId("student1");
        });

        assertEquals("Student is not exists!", exception.getMessage());
        verify(studentRepo, times(1)).findById("student1");
    }
}
