package com.smartEdu.SmartEduBackend.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "zonalMinistryOfEducation")
public class ZonalMinistryOfEducation {
    @Id
    private String id;

    private String district;
    private String zonal;
    private String address;

    private List<School> schools = new ArrayList<>();
}
