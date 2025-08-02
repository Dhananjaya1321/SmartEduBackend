package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.enums.SchoolStatus;
import com.smartEdu.SmartEduBackend.repo.ClassRoomRepo;
import com.smartEdu.SmartEduBackend.repo.GradesRepo;
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
    private ClassRoomRepo classRoomRepo;


    public GradesRequest saveGrades(GradesRequest request, String token) {
        String schoolId = jwtUtil.extractInstitutionId(token);

        String[] parts = request.getGradeSpan().split("-");
        int start = Integer.parseInt(parts[0]);
        int end = Integer.parseInt(parts[1]);

        for (int i = 0; i < end; i++) {
            Grades grades = Grades.builder()
                    .gradeName(start)
                    .schoolId(schoolId)
                    .build();

            if (start==12 || start==13) {
                grades.setStreamsOfALs(request.getStreamsOfALs());
            }
            start++;
            gradesRepo.save(grades);
        }

        return request;
    }
    public List<GradesResponse> getAllGrades(String token) {
        String institutionId = jwtUtil.extractInstitutionId(token);
        List<Grades> allBySchoolId = gradesRepo.findAllBySchoolId(institutionId);

        List<GradesResponse> gradesResponses = new ArrayList<>();

        for (Grades grade : allBySchoolId) {
            List<ClassRoom> classRooms = new ArrayList<>();

            if (grade.getClassIds() != null && !grade.getClassIds().isEmpty()) {
                for (String classId : grade.getClassIds()) {
                    ClassRoom classRoom = classRoomRepo.findById(classId).orElse(null);
                    if (classRoom != null) {
                        classRooms.add(classRoom);
                    }
                }
            }


            GradesResponse gradesResponse = GradesResponse.builder()
                    .id(grade.getId())
                    .gradeName(grade.getGradeName())
                    .classRooms(classRooms)
                    .streamsOfALs(grade.getStreamsOfALs())
                    .build();

            gradesResponses.add(gradesResponse);
        }
        return gradesResponses;
    }

}


