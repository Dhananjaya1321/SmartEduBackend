package com.smartEdu.SmartEduBackend.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "zonal_education_offices")
public class ZonalEducationOffice {
    @Id
    private String id;

    private String province;
    private String district;
    private String zonal;
    private String officeAddress;
    private String fullName;

    private List<String> schoolsIds = new ArrayList<>();
}
