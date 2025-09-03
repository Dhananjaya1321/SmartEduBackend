package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.Event;
import com.smartEdu.SmartEduBackend.entity.Grades;
import com.smartEdu.SmartEduBackend.entity.Parent;
import com.smartEdu.SmartEduBackend.entity.Student;
import com.smartEdu.SmartEduBackend.entity.User;
import com.smartEdu.SmartEduBackend.repo.EventRepo;
import com.smartEdu.SmartEduBackend.repo.GradesRepo;
import com.smartEdu.SmartEduBackend.repo.ParentRepo;
import com.smartEdu.SmartEduBackend.repo.StudentRepo;
import com.smartEdu.SmartEduBackend.repo.UserRepo;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class EventServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepo userRepo;

    @Mock
    private GradesRepo gradesRepo;

    @Mock
    private StudentRepo studentRepo;

    @Mock
    private ParentRepo parentRepo;

    @Mock
    private EventRepo eventRepo;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave_ValidEventAndToken_SavesEvent() {
        String token = "validToken";
        String institutionId = "school123";
        Event event = Event.builder().name("Test Event").build();
        Event savedEvent = Event.builder().id("event1").name("Test Event").schoolId(institutionId).build();

        when(jwtUtil.extractInstitutionId(token)).thenReturn(institutionId);
        when(eventRepo.save(any(Event.class))).thenReturn(savedEvent);

        Event result = eventService.save(event, token);

        assertEquals(savedEvent, result);
        assertEquals(institutionId, result.getSchoolId());
        verify(eventRepo, times(1)).save(event);
    }

    @Test
    void testUpdate_ExistingEvent_UpdatesEvent() {
        String id = "event1";
        String schoolId = "school123";
        Event existingEvent = Event.builder().id(id).schoolId(schoolId).name("Old Event").build();
        Event updatedEvent = Event.builder().name("New Event").build();
        Event savedEvent = Event.builder().id(id).schoolId(schoolId).name("New Event").build();

        when(eventRepo.findById(id)).thenReturn(Optional.of(existingEvent));
        when(eventRepo.save(any(Event.class))).thenReturn(savedEvent);

        Event result = eventService.update(id, updatedEvent);

        assertEquals(savedEvent, result);
        assertEquals(id, result.getId());
        assertEquals(schoolId, result.getSchoolId());
        verify(eventRepo, times(1)).save(any(Event.class));
    }

    @Test
    void testUpdate_NonExistingEvent_ThrowsRuntimeException() {
        String id = "nonExisting";
        Event updatedEvent = Event.builder().name("New Event").build();

        when(eventRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> eventService.update(id, updatedEvent));
        verify(eventRepo, never()).save(any(Event.class));
    }

    @Test
    void testDelete_ExistingEvent_DeletesEvent() {
        String id = "event1";
        Event event = Event.builder().id(id).build();

        when(eventRepo.findById(id)).thenReturn(Optional.of(event));

        eventService.delete(id);

        verify(eventRepo, times(1)).delete(event);
    }

    @Test
    void testDelete_NonExistingEvent_ThrowsRuntimeException() {
        String id = "nonExisting";

        when(eventRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> eventService.delete(id));
        verify(eventRepo, never()).delete(any(Event.class));
    }

    @Test
    void testFindById_ExistingEvent_ReturnsEvent() {
        String id = "event1";
        Event event = Event.builder().id(id).name("Test Event").build();

        when(eventRepo.findById(id)).thenReturn(Optional.of(event));

        Optional<Event> result = eventService.findById(id);

        assertTrue(result.isPresent());
        assertEquals(event, result.get());
    }

    @Test
    void testFindById_NonExistingEvent_ThrowsRuntimeException() {
        String id = "nonExisting";

        when(eventRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> eventService.findById(id));
    }

    @Test
    void testFindAll_ValidTokenAndPage_ReturnsEventList() {
        String token = "validToken";
        String institutionId = "school123";
        int page = 0, size = 10;
        Event event = Event.builder().id("event1").name("Test Event").build();
        List<Event> events = List.of(event);
        Page<Event> pageResult = new PageImpl<>(events);

        when(jwtUtil.extractInstitutionId(token)).thenReturn(institutionId);
        when(eventRepo.findAll(any(PageRequest.class))).thenReturn(pageResult);

        List<Event> result = eventService.findAll(page, size, token);

        assertEquals(events, result);
        verify(eventRepo, times(1)).findAll(PageRequest.of(page, size));
    }

    @Test
    void testGetEventsByGrade_ValidToken_ReturnsEvents() {
        String token = "validToken";
        String username = "user1";
        String profileId = "parent1";
        String studentId = "student1";
        String gradeId = "grade1";
        String schoolId = "school123";
        String gradeName = "5";
        User user = User.builder().username(username).profileId(profileId).build();
        Parent parent = Parent.builder().id(profileId).studentIds(List.of(studentId)).build();
        Student student = Student.builder().id(studentId).gradeId(gradeId).schoolId(schoolId).build();
        Grades grades = Grades.builder().id(gradeId).gradeName(gradeName).build();
        Event event = Event.builder().id("event1").grades("grade_1_to_5").build();
        List<Event> events = List.of(event);

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(parentRepo.findById(profileId)).thenReturn(Optional.of(parent));
        when(studentRepo.findById(studentId)).thenReturn(Optional.of(student));
        when(gradesRepo.findById(gradeId)).thenReturn(Optional.of(grades));
        when(eventRepo.findByGradesAndSchoolId("grade_1_to_5", schoolId)).thenReturn(events);

        List<Event> result = eventService.getEventsByGrade(token);

        assertEquals(events, result);
        verify(eventRepo, times(1)).findByGradesAndSchoolId("grade_1_to_5", schoolId);
    }

    @Test
    void testGetEventsByGrade_InvalidGrade_ThrowsIllegalArgumentException() {
        String token = "validToken";
        String username = "user1";
        String profileId = "parent1";
        String studentId = "student1";
        String gradeId = "grade1";
        String schoolId = "school123";
        String gradeName = "invalid";
        User user = User.builder().username(username).profileId(profileId).build();
        Parent parent = Parent.builder().id(profileId).studentIds(List.of(studentId)).build();
        Student student = Student.builder().id(studentId).gradeId(gradeId).schoolId(schoolId).build();
        Grades grades = Grades.builder().id(gradeId).gradeName(gradeName).build();

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(parentRepo.findById(profileId)).thenReturn(Optional.of(parent));
        when(studentRepo.findById(studentId)).thenReturn(Optional.of(student));
        when(gradesRepo.findById(gradeId)).thenReturn(Optional.of(grades));

        assertThrows(IllegalArgumentException.class, () -> eventService.getEventsByGrade(token));
    }

    @Test
    void testGetEventsToTeacher_ValidToken_ReturnsEvents() {
        String token = "validToken";
        String institutionId = "school123";
        Event event = Event.builder().id("event1").grades("all_grade").build();
        List<Event> events = List.of(event);

        when(jwtUtil.extractInstitutionId(token)).thenReturn(institutionId);
        when(eventRepo.findByGradesAndSchoolId("all_grade", institutionId)).thenReturn(events);

        List<Event> result = eventService.getEventsToTeacher(token);

        assertEquals(events, result);
        verify(eventRepo, times(1)).findByGradesAndSchoolId("all_grade", institutionId);
    }

    @Test
    void testGetMatchingGrades_ValidGrade_ReturnsMatchingGrades() {
        String input = "5";
        List<String> expected = Arrays.asList("grade_5", "grade_1_to_5", "grade_1_to_9", "all_grade");

        List<String> result = EventService.getMatchingGrades(input);

        assertEquals(expected, result);
    }

    @Test
    void testGetMatchingGrades_InvalidGrade_ThrowsIllegalArgumentException() {
        String input = "14";

        assertThrows(IllegalArgumentException.class, () -> EventService.getMatchingGrades(input));
    }

    @Test
    void testGetMatchingGrades_NonNumericGrade_ThrowsIllegalArgumentException() {
        String input = "invalid";

        assertThrows(IllegalArgumentException.class, () -> EventService.getMatchingGrades(input));
    }
}
