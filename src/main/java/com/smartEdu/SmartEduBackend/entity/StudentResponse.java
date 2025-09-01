package com.smartEdu.SmartEduBackend.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StudentResponse {
    @Id
    private String id;

    // Basic Info
    private LocalDate entryDate;
    private String fullName;
    private String fullNameWithInitials;
    private LocalDate dateOfBirth;

    // Parent Info
    private String motherName;
    private String motherContact;
    private String fatherName;
    private String fatherContact;
    private String address;

    // Other Info
    private String registrationNumber;
    private String gradeId;
    private String gradeName;
    private String schoolId;
    private String classId;
    private String className;

    private List<Achievements> achievements = new ArrayList<>();
}
