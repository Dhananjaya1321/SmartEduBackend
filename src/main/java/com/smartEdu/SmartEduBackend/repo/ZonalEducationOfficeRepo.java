package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.ZonalEducationOffice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ZonalEducationOfficeRepo extends MongoRepository<ZonalEducationOffice, String> {
    ZonalEducationOffice findByZonal(String zonal);
}
