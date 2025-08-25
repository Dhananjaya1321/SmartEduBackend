package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.Exam;
import com.smartEdu.SmartEduBackend.enums.ExamLevel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepo extends MongoRepository<Exam, String> {
    List<Exam> findByGrade(String grade);

    List<Exam> findByYear(String year);

    List<Exam> findByLevel(ExamLevel level);

    List<Exam> findByLevelAndInstitutionId(ExamLevel level, String institutionId);

    List<Exam> findByInstitutionId(String institutionId);

    List<Exam> findByGradeAndInstitutionIdAndYear(String gradeName, String institutionId,String year);

    List<Exam> findByGradeAndLevelAndYear(String gradeName,ExamLevel level, String year);

    List<Exam> findByGradeAndLevelAndYearAndExamName(String number, ExamLevel examLevel, String year, String examName);

    List<Exam> findByInstitutionIdAndYear(String institutionId, String year);

    List<Exam> findByLevelAndYear(ExamLevel examLevel, String year);

    List<Exam> findByLevelAndExamName(ExamLevel examLevel, String examName);
}
