package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.ExamResults;
import com.smartEdu.SmartEduBackend.entity.SubjectResults;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamResultsRepo extends MongoRepository<ExamResults, String> {
    ExamResults findByExamIdAndSchoolIdAndGradeIdAndClassId(String id, String institutionId, String gradeId, String classId);

    ExamResults findByExamIdAndSchoolIdAndGradeId(String id, String institutionId, String gradeId);
}
