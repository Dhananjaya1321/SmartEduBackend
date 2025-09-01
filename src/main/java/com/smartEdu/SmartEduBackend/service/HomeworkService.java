package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.Homeworks;
import com.smartEdu.SmartEduBackend.entity.Parent;
import com.smartEdu.SmartEduBackend.entity.Student;
import com.smartEdu.SmartEduBackend.entity.User;
import com.smartEdu.SmartEduBackend.repo.*;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HomeworkService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ParentRepo parentRepo;

    @Autowired
    private HomeworkRepo homeworkRepo;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private ClassRoomRepo classRoomRepo;

    public Homeworks saveHomework(Homeworks homework) {
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

    public List<Homeworks> getHomeworksToParents(String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username).get();
        String profileId = user.getProfileId();

        Parent parent = parentRepo.findById(profileId).get();
        Student student = studentRepo.findById(parent.getStudentIds().getFirst()).get();

        return homeworkRepo.findByClassId(student.getClassId());
    }
}
