package com.smartEdu.SmartEduBackend.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "classes")
public class ClassRoom {
    @Id
    private String id;

    private String className;
    private String grade;
    private String classTeacherId; // Link to a Teacher or User ID

    private List<String> studentIds; // List of student _ids
}
