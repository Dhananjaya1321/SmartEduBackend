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
@Document(collection = "parents")
public class Parent {
    @Id
    private String id;

    private String fullName;

    private List<String> studentIds; // Links to their children
}
