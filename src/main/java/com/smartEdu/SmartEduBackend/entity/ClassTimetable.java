package com.smartEdu.SmartEduBackend.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "timetables")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassTimetable {
    @Id
    private String id;

    private String classId; // Linked to ClassRoom
    private String grade;

    // Monday to Friday -> 8 periods per day
    private Map<String, List<Period>> schedule;
    // e.g., "monday" -> List<Period>, "tuesday" -> List<Period>, etc.
}
