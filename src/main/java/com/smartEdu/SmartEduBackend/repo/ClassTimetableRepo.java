package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.ClassTimetable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassTimetableRepo extends MongoRepository<ClassTimetable, String> {
    Optional<ClassTimetable> findByClassId(String classId);

    List<ClassTimetable> findBySchoolId(String schoolId);
}
