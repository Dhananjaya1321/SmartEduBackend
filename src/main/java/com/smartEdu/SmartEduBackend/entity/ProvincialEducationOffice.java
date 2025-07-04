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
@Document(collection = "provincial_education_offices")
public class ProvincialEducationOffice {
    @Id
    private String id;

    private String province;
    private String officeAddress;
    private String name;

    private List<ZonalEducationOffice> zonalOffices = new ArrayList<>();
}
