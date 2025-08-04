package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentRepo extends MongoRepository<Student, String> {
    Optional<Student> findByRegistrationNumber(String registrationNumber);

    // Counts students whose entryDate is between start and end
    long countBySchoolIdAndEntryDateBetween(String schoolId, LocalDate start, LocalDate end);

    List<Student> findAllBySchoolId(String schoolId);
}
