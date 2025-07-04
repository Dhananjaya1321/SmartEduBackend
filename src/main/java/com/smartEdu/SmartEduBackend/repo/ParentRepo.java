package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.Parent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ParentRepo extends MongoRepository<Parent, String> {
}
