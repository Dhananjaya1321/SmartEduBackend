package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.Principal;
import com.smartEdu.SmartEduBackend.entity.School;
import com.smartEdu.SmartEduBackend.entity.Teacher;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TeacherRepo extends MongoRepository<Teacher, String> {

    Optional<Teacher> findBySchoolId(String schoolId);

    List<Teacher> findAllBySchoolId(String schoolId);
}

