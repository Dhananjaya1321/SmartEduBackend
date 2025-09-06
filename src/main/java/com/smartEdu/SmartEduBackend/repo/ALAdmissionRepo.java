package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.ALAdmission;
import com.smartEdu.SmartEduBackend.entity.Achievements;
import com.smartEdu.SmartEduBackend.enums.ALAdmissionStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ALAdmissionRepo extends MongoRepository<ALAdmission, String> {
    List<ALAdmission> findByStudentId(String studentId);

    List<ALAdmission> findBySchoolId(String institutionId);

    List<ALAdmission> findBySchoolIdAndStatusAndYear(String schoolId,ALAdmissionStatus status,String year);
}
