package com.smartEdu.SmartEduBackend.entity;

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
public class ALAdmissionRequest {
    private String studentId;
    private String indexNumber;
    private String year;
    private String subjectStream;
    private List<String> schoolIds;
}
