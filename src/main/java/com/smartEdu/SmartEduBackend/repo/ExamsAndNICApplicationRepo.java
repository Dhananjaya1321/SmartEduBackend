package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.ExamsAndNICApplication;
import com.smartEdu.SmartEduBackend.entity.ExamsAndNICApplicationResponse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamsAndNICApplicationRepo extends MongoRepository<ExamsAndNICApplication, String> {
    List<ExamsAndNICApplication> findByTypeAndSchoolId(String applicationType, String schoolId);

    List<ExamsAndNICApplicationResponse> findByStudentId(String id);

}
