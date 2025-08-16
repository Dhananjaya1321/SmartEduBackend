package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.School;
import com.smartEdu.SmartEduBackend.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SchoolRepo extends MongoRepository<School, String> {

    Page<School> findAllByStatus(Pageable pageable);

    Optional<School> findByProvinceAndDistrictAndZonal(String province,String district,String zonal);
}

