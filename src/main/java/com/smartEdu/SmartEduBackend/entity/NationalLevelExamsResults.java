package com.smartEdu.SmartEduBackend.entity;


import com.smartEdu.SmartEduBackend.enums.ExamLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "national_level_exams_results")
public class NationalLevelExamsResults {
    @Id
    private String examName;
    private String indexNumber;
    private String studentName;
    private String syllabus;
    private String year;

    private String marks; // Specific to Grade 5 Scholarship
    private String cutOffMarks; // Specific to Grade 5 Scholarship

    private String districtRank; // Specific to G.C.E. (A/L)
    private String islandRank; // Specific to G.C.E. (A/L)
    private String stream; // Specific to G.C.E. (A/L)
    private String zScore; // Specific to G.C.E. (A/L)

    private List<NationalLevelExamsResult> examsResults = new ArrayList<>();
}
