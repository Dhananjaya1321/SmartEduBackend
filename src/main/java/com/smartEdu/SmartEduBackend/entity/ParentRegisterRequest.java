package com.smartEdu.SmartEduBackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentRegisterRequest {
    private String fullName;
    private String nic;
    private String address;
    private String contact;
    private String email;

    private String username;
    private String password;
}
