package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.repo.*;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GradesServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private GradesRepo gradesRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ClassTimetableRepo classTimetableRepo;

    @Mock
    private TeacherRepo teacherRepo;

    @Mock
    private ClassRoomRepo classRoomRepo;

    @InjectMocks
    private GradesService gradesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveGrades_RegularGrades_SavesGrades() {
        String token = "validToken";
        String schoolId = "school123";
        GradesRequest request = GradesRequest.builder()
                .gradeSpan("1-5")
                .streamsOfALs(Arrays.asList())
                .build();
        Grades grade1 = Grades.builder().gradeName("1").schoolId(schoolId).build();
        Grades grade2 = Grades.builder().gradeName("2").schoolId(schoolId).build();

        when(jwtUtil.extractInstitutionId(token)).thenReturn(schoolId);
        when(gradesRepo.save(any(Grades.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GradesRequest result = gradesService.saveGrades(request, token);

        assertEquals(request, result);
        verify(gradesRepo, times(5)).save(any(Grades.class));
    }

    @Test
    void testSaveGrades_ALGrades_SavesGradesWithStreams() {
        String token = "validToken";
        String schoolId = "school123";
        GradesRequest request = GradesRequest.builder()
                .gradeSpan("12-13")
                .streamsOfALs(Arrays.asList("Science", "Arts"))
                .build();
        Grades grade12Science = Grades.builder().gradeName("12(Science)").schoolId(schoolId).build();
        Grades grade12Arts = Grades.builder().gradeName("12(Arts)").schoolId(schoolId).build();
        Grades grade13Science = Grades.builder().gradeName("13(Science)").schoolId(schoolId).build();
        Grades grade13Arts = Grades.builder().gradeName("13(Arts)").schoolId(schoolId).build();

        when(jwtUtil.extractInstitutionId(token)).thenReturn(schoolId);
        when(gradesRepo.save(any(Grades.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GradesRequest result = gradesService.saveGrades(request, token);

        assertEquals(request, result);
        verify(gradesRepo, times(4)).save(any(Grades.class));
    }

    @Test
    void testGetAllGrades_ValidToken_ReturnsGradesResponses() {
        String token = "validToken";
        String schoolId = "school123";
        String gradeId = "grade1";
        String classId = "class1";
        String teacherId = "teacher1";
        Grades grade = Grades.builder()
                .id(gradeId)
                .gradeName("5")
                .schoolId(schoolId)
                .classIds(Arrays.asList(classId))
                .build();
        ClassRoom classRoom = ClassRoom.builder()
                .id(classId)
                .className("5A")
                .gradeId(gradeId)
                .classTeacherId(teacherId)
                .classTeacherSubject("Math")
                .studentIds(Arrays.asList("student1"))
                .build();
        Teacher teacher = Teacher.builder()
                .id(teacherId)
                .fullName("John Doe")
                .build();

        when(jwtUtil.extractInstitutionId(token)).thenReturn(schoolId);
        when(gradesRepo.findAllBySchoolId(schoolId)).thenReturn(Arrays.asList(grade));
        when(classRoomRepo.findById(classId)).thenReturn(Optional.of(classRoom));
        when(teacherRepo.findById(teacherId)).thenReturn(Optional.of(teacher));

        List<GradesResponse> result = gradesService.getAllGrades(token);

        assertEquals(1, result.size());
        assertEquals(gradeId, result.get(0).getId());
        assertEquals("5", result.get(0).getGradeName());
        assertEquals(1, result.get(0).getClassRooms().size());
        assertEquals("5A", result.get(0).getClassRooms().get(0).getClassName());
        assertEquals("John Doe", result.get(0).getClassRooms().get(0).getClassTeacherName());
        verify(gradesRepo, times(1)).findAllBySchoolId(schoolId);
        verify(classRoomRepo, times(1)).findById(classId);
        verify(teacherRepo, times(1)).findById(teacherId);
    }

    @Test
    void testGetAllGradesWithTimetables_ValidToken_ReturnsGradesWithTimetables() {
        String token = "validToken";
        String schoolId = "school123";
        String gradeId = "grade1";
        String classId = "class1";
        String teacherId = "teacher1";
        Grades grade = Grades.builder()
                .id(gradeId)
                .gradeName("5")
                .schoolId(schoolId)
                .classIds(Arrays.asList(classId))
                .build();
        ClassRoom classRoom = ClassRoom.builder()
                .id(classId)
                .className("5A")
                .gradeId(gradeId)
                .classTeacherId(teacherId)
                .classTeacherSubject("Math")
                .studentIds(Arrays.asList("student1"))
                .build();
        Teacher teacher = Teacher.builder()
                .id(teacherId)
                .fullName("John Doe")
                .build();
        ClassTimetable timetable = ClassTimetable.builder()
                .classId(classId)
                .timetablePeriods(Arrays.asList())
                .build();

        when(jwtUtil.extractInstitutionId(token)).thenReturn(schoolId);
        when(gradesRepo.findAllBySchoolId(schoolId)).thenReturn(Arrays.asList(grade));
        when(classRoomRepo.findById(classId)).thenReturn(Optional.of(classRoom));
        when(teacherRepo.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(classTimetableRepo.findByClassId(classId)).thenReturn(Optional.of(timetable));

        List<GradesResponse> result = gradesService.getAllGradesWithTimetables(token);

        assertEquals(1, result.size());
        assertEquals(gradeId, result.get(0).getId());
        assertEquals("5", result.get(0).getGradeName());
        assertEquals(1, result.get(0).getClassRooms().size());
        assertEquals("5A", result.get(0).getClassRooms().get(0).getClassName());
        assertEquals(timetable, result.get(0).getClassRooms().get(0).getTimetable());
        verify(gradesRepo, times(1)).findAllBySchoolId(schoolId);
        verify(classRoomRepo, times(1)).findById(classId);
        verify(teacherRepo, times(1)).findById(teacherId);
        verify(classTimetableRepo, times(1)).findByClassId(classId);
    }

    @Test
    void testGetAllGradesITeach_ValidToken_ReturnsGradesWithClassesTeacherTeaches() {
        String token = "validToken";
        String username = "user1";
        String schoolId = "school123";
        String profileId = "teacher1";
        String classId = "class1";
        String gradeId = "grade1";
        User user = User.builder().username(username).profileId(profileId).build();
        ClassTimetable timetable = ClassTimetable.builder()
                .classId(classId)
                .schoolId(schoolId)
                .timetablePeriods(Arrays.asList(
                        TimetablePeriod.builder()
                                .slots(Arrays.asList(
                                        TimetableSlot.builder().teacherId(profileId).subject("Math").build()
                                ))
                                .build()
                ))
                .build();
        ClassRoom classRoom = ClassRoom.builder()
                .id(classId)
                .className("5A")
                .gradeId(gradeId)
                .build();
        Grades grade = Grades.builder()
                .id(gradeId)
                .gradeName("5")
                .schoolId(schoolId)
                .build();

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(jwtUtil.extractInstitutionId(token)).thenReturn(schoolId);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(classTimetableRepo.findBySchoolId(schoolId)).thenReturn(Arrays.asList(timetable));
        when(classRoomRepo.findById(classId)).thenReturn(Optional.of(classRoom));
        when(gradesRepo.findById(gradeId)).thenReturn(Optional.of(grade));

        List<GradesResponse> result = gradesService.getAllGradesITeach(token);

        assertEquals(1, result.size());
        assertEquals(gradeId, result.get(0).getId());
        assertEquals("5", result.get(0).getGradeName());
        assertEquals(1, result.get(0).getClassRooms().size());
        assertEquals("5A", result.get(0).getClassRooms().get(0).getClassName());
        assertEquals("Math", result.get(0).getClassRooms().get(0).getClassTeacherSubject());
        verify(classTimetableRepo, times(1)).findBySchoolId(schoolId);
        verify(classRoomRepo, times(1)).findById(classId);
        verify(gradesRepo, times(1)).findById(gradeId);
    }

    @Test
    void testGetAllGradesITeach_NoMatchingTeacher_ReturnsEmptyList() {
        String token = "validToken";
        String username = "user1";
        String schoolId = "school123";
        String profileId = "teacher1";
        User user = User.builder().username(username).profileId(profileId).build();
        ClassTimetable timetable = ClassTimetable.builder()
                .classId("class1")
                .schoolId(schoolId)
                .timetablePeriods(Arrays.asList(
                        TimetablePeriod.builder()
                                .slots(Arrays.asList(
                                        TimetableSlot.builder().teacherId("otherTeacher").subject("Math").build()
                                ))
                                .build()
                ))
                .build();

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(jwtUtil.extractInstitutionId(token)).thenReturn(schoolId);
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(classTimetableRepo.findBySchoolId(schoolId)).thenReturn(Arrays.asList(timetable));

        List<GradesResponse> result = gradesService.getAllGradesITeach(token);

        assertEquals(0, result.size());
        verify(classTimetableRepo, times(1)).findBySchoolId(schoolId);
        verify(classRoomRepo, never()).findById(anyString());
        verify(gradesRepo, never()).findById(anyString());
    }
}
