package com.smartEdu.SmartEduBackend.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "parents")
public class Parent {
    @Id
    private String id;

    private String fullName;
    private String nic;
    private String address;
    private String contact;
    private String email;

    private List<String> studentIds; // Links to their children
}
