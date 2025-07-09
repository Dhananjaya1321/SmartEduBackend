package com.smartEdu.SmartEduBackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchoolRequest {

    // Principal Info
    private String fullName;
    private String nic;
    private String contact;
    private String username;
    private String password;
    private String email;
    private String address;

    // Verification Documents (These will store URLs or base64 strings)
    private String nicFront;
    private String nicBack;
    private String moeFront;
    private String moeBack;
    private String appointment;

    // School Details
    private String schoolName;
    private String logoUrl;
    private String province;
    private String district;
    private String zonal;

    private String schoolLevel;
    private String schoolType;
    private String gradeSpan;

    private String gender;
    private String ethnicity;
    private String languageMedium;

    private String studentPopulation;
    private String teacherPopulation;
    private int classCount;
}
