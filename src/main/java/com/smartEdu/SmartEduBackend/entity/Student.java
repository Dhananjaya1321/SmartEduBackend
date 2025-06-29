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
@Document(collection = "students")
public class Student {
    @Id
    private String id;
    private String schoolId;

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
    private String grade;
    private String className;


}
