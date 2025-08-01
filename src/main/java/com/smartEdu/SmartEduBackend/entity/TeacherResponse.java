package com.smartEdu.SmartEduBackend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherResponse {
    // Teacher fields
    private String id;
    private String fullName;
    private String schoolName;
    private String schoolId;

    // User fields
    private String nic;
    private String contact;
    private String username;
    private String password;
    private String address;
    private String email;
}
