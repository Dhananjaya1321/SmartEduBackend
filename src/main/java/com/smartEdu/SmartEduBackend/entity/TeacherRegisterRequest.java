package com.smartEdu.SmartEduBackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherRegisterRequest {
    // Teacher fields
    private String fullName;
    private String schoolId;

    // User fields
    private String nic;
    private String contact;
    private String username;
    private String password;
    private String address;
    private String email;
}
