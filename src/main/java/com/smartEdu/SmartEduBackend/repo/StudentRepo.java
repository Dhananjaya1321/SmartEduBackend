package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentRepo extends MongoRepository<Student, String> {
}
