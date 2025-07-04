package com.smartEdu.SmartEduBackend.entity;

import com.smartEdu.SmartEduBackend.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrincipalRegisterRequest {
    // Principal fields
    private String schoolId;
    private String fullName;
    private String moeId;
    private String nicFrontImageUrl;
    private String nicBackImageUrl;
    private String moeIdFrontImageUrl;
    private String moeIdBackImageUrl;
    private String appointmentLetterUrl;

    // User fields
    private String nic;
    private String contact;
    private String username;
    private String password;
    private String address;
    private String email;
}
