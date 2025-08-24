package com.smartEdu.SmartEduBackend.entity;

import com.smartEdu.SmartEduBackend.enums.ExamsResults;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "exam_results")
public class ExamResults {
    @Id
    private String id;
    private String classId;
    private String schoolId;
    private String gradeId;
    private String gradeName;
    private String examId;
    private String examName;
    private ExamsResults examResultsStatus;
    private String year;
}
