package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.Principal;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PrincipalRepo extends MongoRepository<Principal, String> {

    Optional<Principal> findBySchoolId(String id);
}

