package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.StudentReport;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentReportRepo extends MongoRepository<StudentReport, String> {
    StudentReport findByStudentId(String studentId);
}
