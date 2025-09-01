package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.Achievements;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AchievementRepo extends MongoRepository<Achievements, String> {
}
