package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.ClassTimetable;
import com.smartEdu.SmartEduBackend.repo.ClassTimetableRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClassTimetableService {

    @Autowired
    private ClassTimetableRepo classTimetableRepo;

    public ClassTimetable save(ClassTimetable timetable) {
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

    public List<ClassTimetable> findByGrade(String grade) {
        return classTimetableRepo.findByGrade(grade);
    }
}
