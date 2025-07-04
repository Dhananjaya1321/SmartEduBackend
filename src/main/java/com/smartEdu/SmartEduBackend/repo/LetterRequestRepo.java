package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.LetterRequest;
import com.smartEdu.SmartEduBackend.enums.LetterStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LetterRequestRepo extends MongoRepository<LetterRequest, String> {
    List<LetterRequest> findByStudentId(String studentId);
    List<LetterRequest> findByStatus(LetterStatus status);
}
