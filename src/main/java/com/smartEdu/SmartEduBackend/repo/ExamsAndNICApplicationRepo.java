package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.ExamsAndNICApplication;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamsAndNICApplicationRepo extends MongoRepository<ExamsAndNICApplication, String> {
}
