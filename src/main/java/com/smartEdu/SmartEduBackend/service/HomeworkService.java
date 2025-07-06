package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.Homeworks;
import com.smartEdu.SmartEduBackend.repo.ClassRoomRepo;
import com.smartEdu.SmartEduBackend.repo.HomeworkRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HomeworkService {
    @Autowired
    private HomeworkRepo homeworkRepo;

    @Autowired
    private ClassRoomRepo classRoomRepo;

    public Homeworks save(Homeworks homework) {
        return homeworkRepo.save(homework);
    }

    public Homeworks update(String id, Homeworks updatedHomework) {
        homeworkRepo.findById(id).orElseThrow(() -> new RuntimeException("Homework is not exists!"));

        updatedHomework.setId(id);
        return homeworkRepo.save(updatedHomework);
    }

    public void delete(String id) {
        homeworkRepo.findById(id).orElseThrow(() -> new RuntimeException("Homework is not exists!"));
        homeworkRepo.deleteById(id);
    }

    public Homeworks getHomeworkById(String id) {
        return homeworkRepo.findById(id).orElse(null);
    }

    public List<Homeworks> getAllHomeworks() {
        return homeworkRepo.findAll();
    }

    public List<Homeworks> getHomeworksByClassId(String classId) {
        classRoomRepo.findById(classId).orElseThrow(() -> new RuntimeException("Class is not exists!"));
        return homeworkRepo.findByClassId(classId);
    }
}
