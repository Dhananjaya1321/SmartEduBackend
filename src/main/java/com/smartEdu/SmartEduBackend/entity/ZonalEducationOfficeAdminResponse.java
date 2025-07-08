package com.smartEdu.SmartEduBackend.entity;

import com.smartEdu.SmartEduBackend.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ZonalEducationOfficeAdminResponse {
    private String id;
    private String contact;
    private String nic;
    private String username;
    private String password;
    private String address;
    private String name;

    private Role role;
    private String email;

    private String province;
    private String district;
    private String zonal;
    private String officeAddress;
}
