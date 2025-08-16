package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.repo.*;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClassTimetableService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private ParentRepo parentRepo;

    @Autowired
    private ClassRoomRepo classRoomRepo;

    @Autowired
    private ClassTimetableRepo classTimetableRepo;

    public ClassTimetable save(ClassTimetable timetable, String token) {
        Optional<ClassTimetable> classTimetable = classTimetableRepo.findByClassId(timetable.getClassId());
        if (classTimetable.isPresent())
            throw new RuntimeException("Timetable is already exists!");

        String institutionId = jwtUtil.extractInstitutionId(token);
        timetable.setSchoolId(institutionId);
        return classTimetableRepo.save(timetable);
    }

    public ClassTimetable update(String id, ClassTimetable timetable) {
        ClassTimetable existing = classTimetableRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Timetable not found!"));
        timetable.setId(id);
        return classTimetableRepo.save(timetable);
    }

    public void delete(String id) {
        classTimetableRepo.deleteById(id);
    }

    public Optional<ClassTimetable> findById(String id) {
        return classTimetableRepo.findById(id);
    }

    public Optional<ClassTimetable> findByClassId(String classId) {
        return classTimetableRepo.findByClassId(classId);
    }

    public List<ClassTimetable> findAll() {
        return classTimetableRepo.findAll();
    }

    public List<ClassTimetable> findAllTimetablesByGradeId(String gradeId) {
        List<ClassTimetable> classTimetables = new ArrayList<>();
        List<ClassRoom> classByGradeName = classRoomRepo.findByGradeId(gradeId);
        for (ClassRoom c : classByGradeName) {
            Optional<ClassTimetable> classTimetable = classTimetableRepo.findByClassId(c.getId());
            classTimetable.ifPresent(classTimetables::add);
        }
        return classTimetables;
    }

    public ClassTimetable findTimetableToParent(String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username).get();
        String profileId = user.getProfileId();

        Parent parent = parentRepo.findById(profileId).get();
        Student student = studentRepo.findById(parent.getStudentIds().getFirst()).get();
        String classId = student.getClassId();

        return classTimetableRepo.findByClassId(classId).get();
    }
}
