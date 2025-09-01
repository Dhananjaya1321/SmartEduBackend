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
@Document(collection = "ministry_of_education_office")
public class MinistryOfEducationOffice {
    @Id
    private String id;
    private String officeAddress;
    private String name;

    private List<ProvincialEducationOffice> provincialOffices = new ArrayList<>();
}
