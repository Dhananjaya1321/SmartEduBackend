package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.repo.*;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class GradesService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private GradesRepo gradesRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ClassTimetableRepo classTimetableRepo;

    @Autowired
    private TeacherRepo teacherRepo;

    @Autowired
    private ClassRoomRepo classRoomRepo;


    public GradesRequest saveGrades(GradesRequest request, String token) {
        String schoolId = jwtUtil.extractInstitutionId(token);

        String[] parts = request.getGradeSpan().split("-");
        int start = Integer.parseInt(parts[0]);
        int end = Integer.parseInt(parts[1]);

        for (int i = 0; i < end; i++) {
            if (start == 12 || start == 13) {
                for (int j = 0; j < request.getStreamsOfALs().size(); j++) {
                    Grades gradesForALs = Grades.builder()
                            .gradeName(start + "(" + request.getStreamsOfALs().get(j) + ")")
                            .schoolId(schoolId)
                            .build();

                    gradesRepo.save(gradesForALs);
                }
                start++;
            } else {
                Grades grades = Grades.builder()
                        .gradeName(String.valueOf(start))
                        .schoolId(schoolId)
                        .build();

                start++;
                gradesRepo.save(grades);
            }
        }

        return request;
    }

    public List<GradesResponse> getAllGrades(String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);
        List<Grades> allBySchoolId = gradesRepo.findAllBySchoolId(institutionId);

        List<GradesResponse> gradesResponses = new ArrayList<>();

        for (Grades grade : allBySchoolId) {
            List<ClassRoomResponse> classRooms = new ArrayList<>();

            if (grade.getClassIds() != null && !grade.getClassIds().isEmpty()) {
                for (String classId : grade.getClassIds()) {
                    ClassRoom classRoom = classRoomRepo.findById(classId).orElse(null);
                    if (classRoom != null) {
                        Teacher teacher = teacherRepo.findById(classRoom.getClassTeacherId()).get();

                        ClassRoomResponse classRoomResponse = ClassRoomResponse.builder()
                                .id(classRoom.getId())
                                .className(classRoom.getClassName())
                                .gradeId(classRoom.getGradeId())
                                .classTeacherId(classRoom.getClassTeacherId())
                                .classTeacherName(teacher.getFullName())
                                .classTeacherSubject(classRoom.getClassTeacherSubject())
                                .studentIds(classRoom.getStudentIds())
                                .build();
                        classRooms.add(classRoomResponse);
                    }
                }
            }


            GradesResponse gradesResponse = GradesResponse.builder()
                    .id(grade.getId())
                    .gradeName(grade.getGradeName())
                    .classRooms(classRooms)
                    .stream(grade.getStream())
                    .build();

            gradesResponses.add(gradesResponse);
        }
        return gradesResponses;
    }

    public List<GradesResponse> getAllGradesWithTimetables(String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);
        List<Grades> allBySchoolId = gradesRepo.findAllBySchoolId(institutionId);

        List<GradesResponse> gradesResponses = new ArrayList<>();

        for (Grades grade : allBySchoolId) {
            List<ClassRoomResponse> classRooms = new ArrayList<>();

            if (grade.getClassIds() != null && !grade.getClassIds().isEmpty()) {
                for (String classId : grade.getClassIds()) {
                    ClassRoom classRoom = classRoomRepo.findById(classId).orElse(null);
                    ClassTimetable classTimetable = classTimetableRepo.findByClassId(classId).orElse(null);
                    if (classRoom != null) {
                        Teacher teacher = teacherRepo.findById(classRoom.getClassTeacherId()).get();

                        ClassRoomResponse classRoomResponse = ClassRoomResponse.builder()
                                .id(classRoom.getId())
                                .className(classRoom.getClassName())
                                .gradeId(classRoom.getGradeId())
                                .classTeacherId(classRoom.getClassTeacherId())
                                .classTeacherName(teacher.getFullName())
                                .classTeacherSubject(classRoom.getClassTeacherSubject())
                                .studentIds(classRoom.getStudentIds())
                                .timetable(classTimetable)
                                .build();
                        classRooms.add(classRoomResponse);
                    }
                }
            }


            GradesResponse gradesResponse = GradesResponse.builder()
                    .id(grade.getId())
                    .gradeName(grade.getGradeName())
                    .classRooms(classRooms)
                    .stream(grade.getStream())
                    .build();

            gradesResponses.add(gradesResponse);
        }
        return gradesResponses;
    }

    public List<GradesResponse> getAllGradesITeach(String token) {
        String username = jwtUtil.extractUsername(token);
        String schoolId = jwtUtil.extractInstitutionId(token);
        User user = userRepo.findByUsername(username).get();
        String profileId = user.getProfileId();

        Map<String, GradesResponse> gradeMap = new HashMap<>(); // key: gradeId
        Map<String, ClassRoomResponse> classMap = new HashMap<>(); // key: gradeId+classId

        List<ClassTimetable> classTimetables = classTimetableRepo.findBySchoolId(schoolId);
        for (ClassTimetable ct : classTimetables) {
            for (TimetablePeriod tp : ct.getTimetablePeriods()) {
                for (TimetableSlot ts : tp.getSlots()) {
                    if (ts.getTeacherId().equals(profileId)) {
                        ClassRoom classRoom = classRoomRepo.findById(ct.getClassId()).get();
                        Grades grades = gradesRepo.findById(classRoom.getGradeId()).get();

                        // Build unique keys
                        String gradeKey = grades.getId();
                        String classKey = gradeKey + "-" + classRoom.getId();

                        // Ensure grade exists in map
                        GradesResponse gradeResponse = gradeMap.computeIfAbsent(
                                gradeKey,
                                k -> GradesResponse.builder()
                                        .id(grades.getId())
                                        .gradeName(grades.getGradeName())
                                        .classRooms(new ArrayList<>())
                                        .build()
                        );

                        // Ensure class exists in map
                        ClassRoomResponse classRoomResponse = classMap.computeIfAbsent(
                                classKey,
                                k -> {
                                    ClassRoomResponse cr = ClassRoomResponse.builder()
                                            .id(classRoom.getId())
                                            .className(classRoom.getClassName())
                                            .classTeacherSubject("") // start empty, will append
                                            .build();
                                    gradeResponse.getClassRooms().add(cr);
                                    return cr;
                                }
                        );

                        // Append subject (comma-separated if multiple)
                        if (classRoomResponse.getClassTeacherSubject() == null || classRoomResponse.getClassTeacherSubject().isEmpty()) {
                            classRoomResponse.setClassTeacherSubject(ts.getSubject());
                        } else if (!classRoomResponse.getClassTeacherSubject().contains(ts.getSubject())) {
                            classRoomResponse.setClassTeacherSubject(
                                    classRoomResponse.getClassTeacherSubject() + ", " + ts.getSubject()
                            );
                        }
                    }
                }
            }
        }

        return new ArrayList<>(gradeMap.values());
    }

}


