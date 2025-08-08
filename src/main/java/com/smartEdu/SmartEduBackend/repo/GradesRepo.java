package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.Grades;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GradesRepo extends MongoRepository<Grades, String> {

    List<Grades> findAllBySchoolId(String id);

    Grades findByGradeName(String gradeName);
}
