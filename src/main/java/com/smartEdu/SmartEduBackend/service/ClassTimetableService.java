package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.repo.*;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
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

    public ClassTimetable findMyClassesTimetableToTeacher(String token) {
        String username = jwtUtil.extractUsername(token);
        String schoolId = jwtUtil.extractInstitutionId(token);
        User user = userRepo.findByUsername(username).orElseThrow();
        String profileId = user.getProfileId();

        // Get all class timetables in the school
        List<ClassTimetable> classTimetables = classTimetableRepo.findBySchoolId(schoolId);

        // Initialize teacher timetable with empty 8 periods
        List<TimetablePeriod> teacherPeriods = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            TimetablePeriod period = TimetablePeriod.builder()
                    .period(i)
                    .slots(new ArrayList<>(Collections.nCopies(5, null))) // 5 days
                    .build();
            teacherPeriods.add(period);
        }

        // Loop through all class timetables
        for (ClassTimetable ct : classTimetables) {
            for (TimetablePeriod tp : ct.getTimetablePeriods()) {
                int periodNumber = tp.getPeriod()-1;

                // Loop through slots (5 days)
                for (int day = 0; day < tp.getSlots().size(); day++) {
                    TimetableSlot slot = tp.getSlots().get(day);

                    if (slot != null && slot.getTeacherId().equals(profileId)) {
                        // Place teacher's slot into their timetable
                        teacherPeriods.get(periodNumber).getSlots().set(day, slot);
                    }
                }
            }
        }

        // Build final timetable object for teacher
        return ClassTimetable.builder()
                .schoolId(schoolId)
                .classId("TEACHER_" + profileId) // pseudo classId to represent teacher
                .timetablePeriods(teacherPeriods)
                .build();
    }

    public ClassTimetable findOtherClassesTimetableToTeacherByClassId(String classId) {
        if (classTimetableRepo.findByClassId(classId).isPresent()){
            return classTimetableRepo.findByClassId(classId).get();
        }
        return null;
    }
}
