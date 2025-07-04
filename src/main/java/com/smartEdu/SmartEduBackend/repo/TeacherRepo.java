package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.School;
import com.smartEdu.SmartEduBackend.entity.Teacher;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TeacherRepo extends MongoRepository<Teacher, String> {

}

