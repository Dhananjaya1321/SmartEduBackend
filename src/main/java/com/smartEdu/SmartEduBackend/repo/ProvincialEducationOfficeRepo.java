package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.ProvincialEducationOffice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProvincialEducationOfficeRepo extends MongoRepository<ProvincialEducationOffice, String> {
}
