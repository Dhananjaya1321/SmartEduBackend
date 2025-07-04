package com.smartEdu.SmartEduBackend.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "applications")
public class ApplicationForm {
    @Id
    private String id;

    private String applicationType; // Enum: G5, NIC, O_L, A_L
    private String studentId;
    private String studentName;
    private String grade;

    private LocalDate submittedDate;
    private String status; // PENDING, APPROVED, REJECTED

    private Map<String, String> documents; // key = doc type, value = file path
}
