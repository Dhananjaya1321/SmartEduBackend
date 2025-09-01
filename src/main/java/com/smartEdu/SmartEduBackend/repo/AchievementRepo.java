package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.Achievements;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface AchievementRepo extends MongoRepository<Achievements, String> {
    List<Achievements> findByStudentId(String studentId);

    List<Achievements> findByStudentIdAndDateBetween(String id, LocalDate start, LocalDate end);
}
