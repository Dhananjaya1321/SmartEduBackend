package com.smartEdu.SmartEduBackend.repo;

import com.smartEdu.SmartEduBackend.entity.SubjectResults;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectResultsRepo extends MongoRepository<SubjectResults, String> {
//    List<SubjectResults> findByExamId(String examId);

    SubjectResults findByExamIdAndSubjectAndGradeIdAndSchoolId(String id, String subject, String gradeId, String schoolId);

    SubjectResults findByExamIdAndSubjectAndGradeIdAndSchoolIdAndClassIdAndYear(String examId, String subject, String gradeId, String schoolId, String classId, String year);
}
