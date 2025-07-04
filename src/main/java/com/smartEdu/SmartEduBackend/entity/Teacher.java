package com.smartEdu.SmartEduBackend.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "teachers")
public class Teacher {
    @Id
    private String id;

    private String fullName;
    private String nic;
    private String address;
    private String contact;
    private String email;

    private String schoolId; // Link to the school
}
