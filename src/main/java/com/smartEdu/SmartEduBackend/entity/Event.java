package com.smartEdu.SmartEduBackend.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "events")
public class Event {
    @Id
    private String id;

    private String eventName;
    private String[] grades;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}
