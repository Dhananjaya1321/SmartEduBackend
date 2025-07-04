package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.ClassRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ClassRoomRepo extends MongoRepository<ClassRoom, String> {
    List<ClassRoom> findByGrade(String grade);
}
