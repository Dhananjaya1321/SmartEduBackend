package com.smartEdu.SmartEduBackend.entity;


import com.smartEdu.SmartEduBackend.enums.ExamLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "events")
public class Event {
    @Id
    private String id;

    private String eventName;
    private String grade;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;

    private String createdBy; // schoolId
    private LocalDateTime createdAt = LocalDateTime.now();
}
