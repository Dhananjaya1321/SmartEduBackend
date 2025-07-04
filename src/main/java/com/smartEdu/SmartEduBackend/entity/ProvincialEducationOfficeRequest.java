package com.smartEdu.SmartEduBackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProvincialEducationOfficeRequest {
    private String province;
    private String PEOAddress;
    private String name;

    private String nic;
    private String contact;
    private String username;
    private String password;
    private String address;
    private String email;
}
