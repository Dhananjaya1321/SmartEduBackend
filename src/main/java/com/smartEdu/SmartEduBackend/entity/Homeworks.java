package com.smartEdu.SmartEduBackend.entity;

import com.smartEdu.SmartEduBackend.enums.AchievementsCategory;
import com.smartEdu.SmartEduBackend.enums.AchievementsLevels;
import com.smartEdu.SmartEduBackend.enums.AchievementsPlace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "homeworks")
public class Homeworks {
    @Id
    private String id;
    private String classId;
    private String gradeId;
    private String year;
    private String description;
    private String document;
    private LocalDate date;
}
