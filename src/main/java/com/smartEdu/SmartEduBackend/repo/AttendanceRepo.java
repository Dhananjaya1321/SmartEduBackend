package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.Attendance;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepo extends MongoRepository<Attendance, String> {
    List<Attendance> findByClassIdAndDate(String classId, LocalDate date);

    List<Attendance> findByClassIdAndDateBetween(String classId, LocalDate start, LocalDate end);

    List<Attendance> findByStudentIdAndDateBetween(String studentId, LocalDate start, LocalDate end);

    List<Attendance> findByStudentId(String studentId);

    List<Attendance> findByClassId(String classId);

    long countDistinctByClassIdAndDateBetween(String classId, LocalDate start, LocalDate end);

    long countDistinctByClassId(String classId);

    Optional findByStudentIdAndDate(String id, LocalDate today);
}
