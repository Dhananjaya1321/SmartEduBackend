package com.smartEdu.SmartEduBackend.entity;

import com.smartEdu.SmartEduBackend.enums.ALAdmissionStatus;
import com.smartEdu.SmartEduBackend.enums.AchievementsCategory;
import com.smartEdu.SmartEduBackend.enums.AchievementsLevels;
import com.smartEdu.SmartEduBackend.enums.AchievementsPlace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "al_admission")
public class ALAdmission {
    @Id
    private String id;
    private String studentId;
    private String indexNumber;
    private String year;
    private String subjectStream;
    private ALAdmissionStatus status;
    private String schoolId;
    private String schoolName;

    private int olResultsScore;
    private int residenceScore;
    private int nationalLevelAchievementsScore;
    private int provincialLevelAchievementsScore;
    private int zonalLevelAchievementsScore;
    private int totalScore;

    private List<String> olResults;
}
