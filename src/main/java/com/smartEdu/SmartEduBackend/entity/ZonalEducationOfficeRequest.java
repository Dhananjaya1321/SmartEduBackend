package com.smartEdu.SmartEduBackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZonalEducationOfficeRequest {
    // Office details
    private String province;
    private String district;
    private String zonal;
    private String officeAddress;
    private String fullName;

    // Admin details
    private String name;
    private String contact;
    private String nic;
    private String username;
    private String email;
    private String address;
}
