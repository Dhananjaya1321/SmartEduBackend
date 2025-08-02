package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.repo.ClassRoomRepo;
import com.smartEdu.SmartEduBackend.repo.GradesRepo;
import com.smartEdu.SmartEduBackend.repo.TeacherRepo;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class GradesService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private GradesRepo gradesRepo;

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
            if (start==12 || start==13) {
                for (int j = 0; j < request.getStreamsOfALs().size(); j++) {
                    Grades gradesForALs = Grades.builder()
                            .gradeName(start+"("+ request.getStreamsOfALs().get(j) +")")
                            .schoolId(schoolId)
                            .build();

                    gradesRepo.save(gradesForALs);
                }
                start++;
            }else {
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

                        ClassRoomResponse classRoomResponse=ClassRoomResponse.builder()
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

}


