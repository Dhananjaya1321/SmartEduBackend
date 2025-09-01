package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.enums.Role;
import com.smartEdu.SmartEduBackend.repo.AchievementRepo;
import com.smartEdu.SmartEduBackend.repo.StudentRepo;
import com.smartEdu.SmartEduBackend.util.PasswordGeneratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AchievementService {

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private AchievementRepo achievementRepo;

    public Achievements save(String studentId, Achievements achievement) {
        Optional<Student> student = studentRepo.findById(studentId);
        if (student.isEmpty())
            throw new RuntimeException("Student is not exists!");

        Achievements achievements = Achievements.builder()
                .studentId(studentId)
                .name(achievement.getName())
                .description(achievement.getDescription())
                .level(achievement.getLevel())
                .place(achievement.getPlace())
                .category(achievement.getCategory())
                .date(achievement.getDate())
                .build();

        Achievements saved = achievementRepo.save(achievements);

        Student student1 = student.get();
        student1.getAchievements().add(saved);
        studentRepo.save(student1);


        return achievement;
    }

    public Achievements update(String achievementId, Achievements updatedAchievement) {
        achievementRepo.findById(achievementId).orElseThrow(() -> new RuntimeException("Achievement is not exists!"));

        updatedAchievement.setId(achievementId);
        return achievementRepo.save(updatedAchievement);
    }

    public void delete(String achievementId) {
        Achievements achievements = achievementRepo.findById(achievementId).orElseThrow(() -> new RuntimeException("Achievement is not exists!"));
        Student student = studentRepo.findById(achievements.getStudentId()).get();
        student.getAchievements().removeIf(a -> a.getId().equals(achievementId));
        studentRepo.save(student);
        achievementRepo.deleteById(achievementId);
    }

    public List<Achievements> getAchievementsByStudentId(String studentId) {
        Optional<Student> optionalStudent = studentRepo.findById(studentId);
        if (optionalStudent.isEmpty()) throw new RuntimeException("Student is not exists!");
        return optionalStudent.get().getAchievements();
    }
}
