package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepo extends MongoRepository<Event, String> {
}
