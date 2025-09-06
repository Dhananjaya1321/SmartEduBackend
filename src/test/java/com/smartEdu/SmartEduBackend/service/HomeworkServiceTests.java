package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.repo.*;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HomeworkServiceTests {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ParentRepo parentRepo;

    @Mock
    private HomeworkRepo homeworkRepo;

    @Mock
    private StudentRepo studentRepo;

    @Mock
    private ClassRoomRepo classRoomRepo;

    @InjectMocks
    private HomeworkService homeworkService;

    private Homeworks homework;
    private User user;
    private Parent parent;
    private Student student;
    private ClassRoom classRoom;

    @BeforeEach
    void setUp() {
        homework = Homeworks.builder()
                .id("hw1")
                .classId("class1")
                .gradeId("grade1")
                .year("2025")
                .description("Math homework")
                .document("doc1")
                .date(LocalDate.now())
                .build();

        user = User.builder()
                .username("parent_user")
                .profileId("parent1")
                .build();

        parent = Parent.builder()
                .id("parent1")
                .studentIds(Arrays.asList("student1"))
                .build();

        student = Student.builder()
                .id("student1")
                .classId("class1")
                .build();

        classRoom = ClassRoom.builder()
                .id("class1")
                .gradeId("grade1")
                .build();
    }

    @Test
    void shouldSaveHomeworkSuccessfully() {
        when(homeworkRepo.save(any(Homeworks.class))).thenReturn(homework);

        Homeworks result = homeworkService.saveHomework(homework);

        assertNotNull(result);
        assertEquals("hw1", result.getId());
        assertEquals("Math homework", result.getDescription());
        verify(homeworkRepo, times(1)).save(homework);
    }

    @Test
    void shouldUpdateHomeworkSuccessfully() {
        Homeworks updatedHomework = Homeworks.builder()
                .id("hw1")
                .classId("class1")
                .description("Updated Math homework")
                .build();
        when(homeworkRepo.findById("hw1")).thenReturn(Optional.of(homework));
        when(homeworkRepo.save(any(Homeworks.class))).thenReturn(updatedHomework);

        Homeworks result = homeworkService.update("hw1", updatedHomework);

        assertNotNull(result);
        assertEquals("hw1", result.getId());
        assertEquals("Updated Math homework", result.getDescription());
        verify(homeworkRepo, times(1)).findById("hw1");
        verify(homeworkRepo, times(1)).save(updatedHomework);
    }

    @Test
    void shouldThrowExceptionWhenHomeworkNotFoundOnUpdate() {
        when(homeworkRepo.findById("hw1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            homeworkService.update("hw1", homework);
        }, "Homework is not exists!");

        verify(homeworkRepo, times(1)).findById("hw1");
        verify(homeworkRepo, never()).save(any(Homeworks.class));
    }

    @Test
    void shouldDeleteHomeworkSuccessfully() {
        when(homeworkRepo.findById("hw1")).thenReturn(Optional.of(homework));
        doNothing().when(homeworkRepo).deleteById("hw1");

        homeworkService.delete("hw1");

        verify(homeworkRepo, times(1)).findById("hw1");
        verify(homeworkRepo, times(1)).deleteById("hw1");
    }

    @Test
    void shouldThrowExceptionWhenHomeworkNotFoundOnDelete() {
        when(homeworkRepo.findById("hw1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            homeworkService.delete("hw1");
        }, "Homework is not exists!");

        verify(homeworkRepo, times(1)).findById("hw1");
        verify(homeworkRepo, never()).deleteById(anyString());
    }

    @Test
    void shouldGetHomeworkByIdSuccessfully() {
        when(homeworkRepo.findById("hw1")).thenReturn(Optional.of(homework));

        Homeworks result = homeworkService.getHomeworkById("hw1");

        assertNotNull(result);
        assertEquals("hw1", result.getId());
        verify(homeworkRepo, times(1)).findById("hw1");
    }

    @Test
    void shouldReturnNullWhenHomeworkNotFoundById() {
        when(homeworkRepo.findById("hw1")).thenReturn(Optional.empty());

        Homeworks result = homeworkService.getHomeworkById("hw1");

        assertNull(result);
        verify(homeworkRepo, times(1)).findById("hw1");
    }

    @Test
    void shouldGetAllHomeworksSuccessfully() {
        List<Homeworks> homeworks = Arrays.asList(homework);
        when(homeworkRepo.findAll()).thenReturn(homeworks);

        List<Homeworks> result = homeworkService.getAllHomeworks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("hw1", result.get(0).getId());
        verify(homeworkRepo, times(1)).findAll();
    }

    @Test
    void shouldGetHomeworksByClassIdSuccessfully() {
        when(classRoomRepo.findById("class1")).thenReturn(Optional.of(classRoom));
        List<Homeworks> homeworks = Arrays.asList(homework);
        when(homeworkRepo.findByClassId("class1")).thenReturn(homeworks);

        List<Homeworks> result = homeworkService.getHomeworksByClassId("class1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("hw1", result.get(0).getId());
        verify(classRoomRepo, times(1)).findById("class1");
        verify(homeworkRepo, times(1)).findByClassId("class1");
    }

    @Test
    void shouldThrowExceptionWhenClassNotFoundForHomeworksByClassId() {
        when(classRoomRepo.findById("class1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            homeworkService.getHomeworksByClassId("class1");
        }, "Class is not exists!");

        verify(classRoomRepo, times(1)).findById("class1");
        verify(homeworkRepo, never()).findByClassId(anyString());
    }

    @Test
    void shouldGetHomeworksToParentsSuccessfully() {
        when(jwtUtil.extractUsername("token123")).thenReturn("parent_user");
        when(userRepo.findByUsername("parent_user")).thenReturn(Optional.of(user));
        when(parentRepo.findById("parent1")).thenReturn(Optional.of(parent));
        when(studentRepo.findById("student1")).thenReturn(Optional.of(student));
        List<Homeworks> homeworks = Arrays.asList(homework);
        when(homeworkRepo.findByClassId("class1")).thenReturn(homeworks);

        List<Homeworks> result = homeworkService.getHomeworksToParents("token123");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("hw1", result.get(0).getId());
        verify(jwtUtil, times(1)).extractUsername("token123");
        verify(userRepo, times(1)).findByUsername("parent_user");
        verify(parentRepo, times(1)).findById("parent1");
        verify(studentRepo, times(1)).findById("student1");
        verify(homeworkRepo, times(1)).findByClassId("class1");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundForHomeworksToParents() {
        when(jwtUtil.extractUsername("token123")).thenReturn("parent_user");
        when(userRepo.findByUsername("parent_user")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            homeworkService.getHomeworksToParents("token123");
        });

        verify(jwtUtil, times(1)).extractUsername("token123");
        verify(userRepo, times(1)).findByUsername("parent_user");
        verify(parentRepo, never()).findById(anyString());
        verify(studentRepo, never()).findById(anyString());
        verify(homeworkRepo, never()).findByClassId(anyString());
    }
}
