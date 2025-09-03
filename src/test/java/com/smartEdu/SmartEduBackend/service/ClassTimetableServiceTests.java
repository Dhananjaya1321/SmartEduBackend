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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClassTimetableServiceTests {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepo userRepo;

    @Mock
    private StudentRepo studentRepo;

    @Mock
    private ParentRepo parentRepo;

    @Mock
    private ClassRoomRepo classRoomRepo;

    @Mock
    private ClassTimetableRepo classTimetableRepo;

    @InjectMocks
    private ClassTimetableService classTimetableService;

    private ClassTimetable timetable;
    private User user;
    private Parent parent;
    private Student student;
    private ClassRoom classRoom;
    private TimetablePeriod period;
    private TimetableSlot slot;

    @BeforeEach
    void setUp() {
        slot = TimetableSlot.builder()
                .subject("Math")
                .teacherId("teacher1")
                .teacherName("John Teacher")
                .build();

        period = TimetablePeriod.builder()
                .period(1)
                .slots(Arrays.asList(slot, null, null, null, null))
                .build();

        timetable = ClassTimetable.builder()
                .id("timetable1")
                .classId("class1")
                .schoolId("school1")
                .timetablePeriods(Arrays.asList(period))
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
    void shouldSaveTimetableSuccessfully() {
        when(classTimetableRepo.findByClassId("class1")).thenReturn(Optional.empty());
        when(jwtUtil.extractInstitutionId("token123")).thenReturn("school1");
        when(classTimetableRepo.save(any(ClassTimetable.class))).thenReturn(timetable);

        ClassTimetable result = classTimetableService.save(timetable, "token123");

        assertNotNull(result);
        assertEquals("timetable1", result.getId());
        assertEquals("school1", result.getSchoolId());
        verify(classTimetableRepo, times(1)).findByClassId("class1");
        verify(jwtUtil, times(1)).extractInstitutionId("token123");
        verify(classTimetableRepo, times(1)).save(timetable);
    }

    @Test
    void shouldThrowExceptionWhenTimetableExists() {
        when(classTimetableRepo.findByClassId("class1")).thenReturn(Optional.of(timetable));

        assertThrows(RuntimeException.class, () -> {
            classTimetableService.save(timetable, "token123");
        }, "Timetable is already exists!");

        verify(classTimetableRepo, times(1)).findByClassId("class1");
        verify(jwtUtil, never()).extractInstitutionId(anyString());
        verify(classTimetableRepo, never()).save(any(ClassTimetable.class));
    }

    @Test
    void shouldUpdateTimetableSuccessfully() {
        ClassTimetable updated = ClassTimetable.builder()
                .id("timetable1")
                .classId("class1")
                .schoolId("school1")
                .timetablePeriods(Arrays.asList(TimetablePeriod.builder().period(2).slots(Arrays.asList(slot)).build()))
                .build();
        when(classTimetableRepo.findById("timetable1")).thenReturn(Optional.of(timetable));
        when(classTimetableRepo.save(any(ClassTimetable.class))).thenReturn(updated);

        ClassTimetable result = classTimetableService.update("timetable1", updated);

        assertNotNull(result);
        assertEquals(2, result.getTimetablePeriods().get(0).getPeriod());
        verify(classTimetableRepo, times(1)).findById("timetable1");
        verify(classTimetableRepo, times(1)).save(updated);
    }

    @Test
    void shouldThrowExceptionWhenTimetableNotFoundOnUpdate() {
        when(classTimetableRepo.findById("timetable1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            classTimetableService.update("timetable1", timetable);
        }, "Timetable not found!");

        verify(classTimetableRepo, times(1)).findById("timetable1");
        verify(classTimetableRepo, never()).save(any(ClassTimetable.class));
    }

    @Test
    void shouldDeleteTimetableSuccessfully() {
        doNothing().when(classTimetableRepo).deleteById("timetable1");

        classTimetableService.delete("timetable1");

        verify(classTimetableRepo, times(1)).deleteById("timetable1");
    }

    @Test
    void shouldFindTimetableByIdSuccessfully() {
        when(classTimetableRepo.findById("timetable1")).thenReturn(Optional.of(timetable));

        Optional<ClassTimetable> result = classTimetableService.findById("timetable1");

        assertTrue(result.isPresent());
        assertEquals("timetable1", result.get().getId());
        verify(classTimetableRepo, times(1)).findById("timetable1");
    }

    @Test
    void shouldReturnEmptyOptionalWhenTimetableNotFoundById() {
        when(classTimetableRepo.findById("timetable1")).thenReturn(Optional.empty());

        Optional<ClassTimetable> result = classTimetableService.findById("timetable1");

        assertFalse(result.isPresent());
        verify(classTimetableRepo, times(1)).findById("timetable1");
    }

    @Test
    void shouldFindTimetableByClassIdSuccessfully() {
        when(classTimetableRepo.findByClassId("class1")).thenReturn(Optional.of(timetable));

        Optional<ClassTimetable> result = classTimetableService.findByClassId("class1");

        assertTrue(result.isPresent());
        assertEquals("class1", result.get().getClassId());
        verify(classTimetableRepo, times(1)).findByClassId("class1");
    }

    @Test
    void shouldReturnEmptyOptionalWhenTimetableNotFoundByClassId() {
        when(classTimetableRepo.findByClassId("class1")).thenReturn(Optional.empty());

        Optional<ClassTimetable> result = classTimetableService.findByClassId("class1");

        assertFalse(result.isPresent());
        verify(classTimetableRepo, times(1)).findByClassId("class1");
    }

    @Test
    void shouldFindAllTimetablesSuccessfully() {
        List<ClassTimetable> timetables = Arrays.asList(timetable);
        when(classTimetableRepo.findAll()).thenReturn(timetables);

        List<ClassTimetable> result = classTimetableService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("timetable1", result.get(0).getId());
        verify(classTimetableRepo, times(1)).findAll();
    }

    @Test
    void shouldFindAllTimetablesByGradeIdSuccessfully() {
        List<ClassRoom> classRooms = Arrays.asList(classRoom);
        when(classRoomRepo.findByGradeId("grade1")).thenReturn(classRooms);
        when(classTimetableRepo.findByClassId("class1")).thenReturn(Optional.of(timetable));

        List<ClassTimetable> result = classTimetableService.findAllTimetablesByGradeId("grade1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("timetable1", result.get(0).getId());
        verify(classRoomRepo, times(1)).findByGradeId("grade1");
        verify(classTimetableRepo, times(1)).findByClassId("class1");
    }

    @Test
    void shouldReturnEmptyListWhenNoTimetablesFoundByGradeId() {
        when(classRoomRepo.findByGradeId("grade1")).thenReturn(Collections.emptyList());

        List<ClassTimetable> result = classTimetableService.findAllTimetablesByGradeId("grade1");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(classRoomRepo, times(1)).findByGradeId("grade1");
        verify(classTimetableRepo, never()).findByClassId(anyString());
    }

    @Test
    void shouldFindTimetableToParentSuccessfully() {
        when(jwtUtil.extractUsername("token123")).thenReturn("parent_user");
        when(userRepo.findByUsername("parent_user")).thenReturn(Optional.of(user));
        when(parentRepo.findById("parent1")).thenReturn(Optional.of(parent));
        when(studentRepo.findById("student1")).thenReturn(Optional.of(student));
        when(classTimetableRepo.findByClassId("class1")).thenReturn(Optional.of(timetable));

        ClassTimetable result = classTimetableService.findTimetableToParent("token123");

        assertNotNull(result);
        assertEquals("timetable1", result.getId());
        verify(jwtUtil, times(1)).extractUsername("token123");
        verify(userRepo, times(1)).findByUsername("parent_user");
        verify(parentRepo, times(1)).findById("parent1");
        verify(studentRepo, times(1)).findById("student1");
        verify(classTimetableRepo, times(1)).findByClassId("class1");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundForParentTimetable() {
        when(jwtUtil.extractUsername("token123")).thenReturn("parent_user");
        when(userRepo.findByUsername("parent_user")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            classTimetableService.findTimetableToParent("token123");
        });

        verify(jwtUtil, times(1)).extractUsername("token123");
        verify(userRepo, times(1)).findByUsername("parent_user");
        verify(parentRepo, never()).findById(anyString());
    }

    @Test
    void shouldFindMyClassesTimetableToTeacherSuccessfully() {
        when(jwtUtil.extractUsername("token123")).thenReturn("teacher_user");
        when(jwtUtil.extractInstitutionId("token123")).thenReturn("school1");
        when(userRepo.findByUsername("teacher_user")).thenReturn(Optional.of(
                User.builder().username("teacher_user").profileId("teacher1").build()
        ));
        List<ClassTimetable> timetables = Arrays.asList(timetable);
        when(classTimetableRepo.findBySchoolId("school1")).thenReturn(timetables);

        ClassTimetable result = classTimetableService.findMyClassesTimetableToTeacher("token123");

        assertNotNull(result);
        assertEquals("TEACHER_teacher1", result.getClassId());
        assertEquals(8, result.getTimetablePeriods().size());
        assertEquals("Math", result.getTimetablePeriods().get(0).getSlots().get(0).getSubject());
        verify(jwtUtil, times(1)).extractUsername("token123");
        verify(jwtUtil, times(1)).extractInstitutionId("token123");
        verify(userRepo, times(1)).findByUsername("teacher_user");
        verify(classTimetableRepo, times(1)).findBySchoolId("school1");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundForTeacherTimetable() {
        when(jwtUtil.extractUsername("token123")).thenReturn("teacher_user");
        when(userRepo.findByUsername("teacher_user")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            classTimetableService.findMyClassesTimetableToTeacher("token123");
        });

        verify(jwtUtil, times(1)).extractUsername("token123");
        verify(userRepo, times(1)).findByUsername("teacher_user");
        verify(classTimetableRepo, never()).findBySchoolId(anyString());
    }

    @Test
    void shouldFindOtherClassesTimetableToTeacherByClassIdSuccessfully() {
        when(classTimetableRepo.findByClassId("class1")).thenReturn(Optional.of(timetable));

        ClassTimetable result = classTimetableService.findOtherClassesTimetableToTeacherByClassId("class1");

        assertNotNull(result);
        assertEquals("timetable1", result.getId());
        verify(classTimetableRepo, times(1)).findByClassId("class1");
    }

    @Test
    void shouldReturnNullWhenClassIdNotFoundForTeacherTimetable() {
        when(classTimetableRepo.findByClassId("class1")).thenReturn(Optional.empty());

        ClassTimetable result = classTimetableService.findOtherClassesTimetableToTeacherByClassId("class1");

        assertNull(result);
        verify(classTimetableRepo, times(1)).findByClassId("class1");
    }
}
