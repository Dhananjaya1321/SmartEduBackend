package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventRepo extends MongoRepository<Event, String> {
    List<Event> findByGrades(String grade);
}
