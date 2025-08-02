package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.ClassRoom;
import com.smartEdu.SmartEduBackend.entity.Grades;
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
public class ClassRoomService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private GradesRepo gradesRepo;

    @Autowired
    private ClassRoomRepo classRoomRepo;

    public ClassRoom createClass(ClassRoom classRoom, String token) {
        ClassRoom save = classRoomRepo.save(classRoom);

        Grades grades = gradesRepo.findById(classRoom.getGradeId()).get();

        if (grades.getClassIds()==null) {
            grades.setClassIds(new ArrayList<>());
        }

        List<String> classIds = grades.getClassIds();
        classIds.add(save.getId());
        grades.setClassIds(classIds);

        gradesRepo.save(grades);
        return classRoomRepo.save(classRoom);
    }

    public List<ClassRoom> createMultipleClasses(List<ClassRoom> classRooms) {
        return classRoomRepo.saveAll(classRooms);
    }

    public List<ClassRoom> getAllClasses() {
        return classRoomRepo.findAll();
    }

    public ClassRoom getClassById(String id) {
        return classRoomRepo.findById(id).orElse(null);
    }

    public List<ClassRoom> getClassesByGrade(String grade) {
        return classRoomRepo.findByGradeId(grade);
    }

    public ClassRoom updateClass(String id, ClassRoom updated) {
        classRoomRepo.findById(id).orElseThrow(() -> new RuntimeException("Class not found!"));
        updated.setId(id);
        return classRoomRepo.save(updated);
    }

    public void deleteClass(String id) {
        classRoomRepo.findById(id).orElseThrow(() -> new RuntimeException("Class not found!"));
        classRoomRepo.deleteById(id);
    }
}
