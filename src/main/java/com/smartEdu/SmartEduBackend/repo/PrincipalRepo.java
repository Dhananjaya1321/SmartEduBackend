package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.Principal;
import com.smartEdu.SmartEduBackend.entity.School;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PrincipalRepo extends MongoRepository<Principal, String> {

}

