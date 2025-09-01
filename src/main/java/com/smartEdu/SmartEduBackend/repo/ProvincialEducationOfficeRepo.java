package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.ProvincialEducationOffice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProvincialEducationOfficeRepo extends MongoRepository<ProvincialEducationOffice, String> {
    ProvincialEducationOffice findByProvince(String province);

    Page<ProvincialEducationOffice> findAllBy(Pageable pageable);
}
