package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.Grades;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GradesRepo extends MongoRepository<Grades, String> {
}
