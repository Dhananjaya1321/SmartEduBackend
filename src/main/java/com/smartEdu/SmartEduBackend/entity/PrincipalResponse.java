package com.smartEdu.SmartEduBackend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrincipalResponse {
    // Principal fields
    private String id;
    private String schoolId;
    private String fullName;
    private String moeId;
    private String nicFrontImageUrl;
    private String nicBackImageUrl;
    private String moeIdFrontImageUrl;
    private String moeIdBackImageUrl;
    private String appointmentLetterUrl;
    private String schoolName;

    // User fields
    private String nic;
    private String contact;
    private String username;
    private String password;
    private String address;
    private String email;
}
