package com.smartEdu.SmartEduBackend.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "zonal_education_offices")
public class ZonalEducationOffice {
    @Id
    private String id;

    private String district;
    private String zonal;
    private String officeAddress;
    private String name; // Admin name
}
