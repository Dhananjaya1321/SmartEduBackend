package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.School;
import com.smartEdu.SmartEduBackend.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SchoolRepo extends MongoRepository<School, String> {

    Page<School> findAllByStatus(Pageable pageable);

    List<School> findByProvinceAndDistrictAndZonal(String province,String district,String zonal);

    @Query("{ 'province': ?1, 'schoolName': { $regex: ?0, $options: 'i' } }")
    List<School> findBySchoolNameAndProvince(String schoolName,String province);

    List<School> findByProvince(String province);
}

