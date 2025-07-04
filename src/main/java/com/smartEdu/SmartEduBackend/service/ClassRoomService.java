package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.ClassRoom;
import com.smartEdu.SmartEduBackend.repo.ClassRoomRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassRoomService {

    @Autowired
    private ClassRoomRepo classRoomRepo;

    public ClassRoom createClass(ClassRoom classRoom) {
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
        return classRoomRepo.findByGrade(grade);
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
