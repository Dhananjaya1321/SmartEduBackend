package com.smartEdu.SmartEduBackend.repo;


import com.smartEdu.SmartEduBackend.entity.NationalLevelExamsResults;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.web.bind.annotation.PathVariable;

public interface NationalLevelExamsResultsRepo extends MongoRepository<NationalLevelExamsResults, String> {
    NationalLevelExamsResults findByIndexNumberAndExamNameAndYear(String indexNumber, String examName, String year);
}
