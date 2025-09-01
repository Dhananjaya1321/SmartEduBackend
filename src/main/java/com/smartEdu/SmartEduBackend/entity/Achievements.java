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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "achievements")
public class Achievements {
    @Id
    private String id;
    private String studentId;
    private String name;
    private String description;
    private AchievementsLevels level;//zonal level, province level, national level
    private AchievementsPlace place;//1st place, 2nd place, 3rd place
    private AchievementsCategory category;//sport, leadership, educational
    private LocalDate date;
}
