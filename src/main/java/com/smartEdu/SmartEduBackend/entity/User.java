package com.smartEdu.SmartEduBackend.entity;

import com.smartEdu.SmartEduBackend.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String contact;
    private String nic;
    private String username;
    private String password;
    private String address;

    private Role role; // Enum: PRINCIPAL, TEACHER, PARENT, STUDENT
    private String email;

    private boolean active = true; // optional

    // Links to role-specific profiles
    private String profileId; // ID pointing to the relevant Principal/Teacher/Parent document
}
