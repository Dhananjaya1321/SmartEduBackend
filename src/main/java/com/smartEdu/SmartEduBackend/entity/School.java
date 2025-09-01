package com.smartEdu.SmartEduBackend.entity;


import com.smartEdu.SmartEduBackend.enums.SchoolStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "school")
public class School {
    @Id
    private String id;
    private Principal principal;

    private String schoolNumber;
    private String schoolName;
    private String logoUrl;

    private String province;
    private String district;
    private String zonal;

    private String levelOfSchool;
    private String typeOfSchool;
    private String gradeSpan;
    private String gender;
    private String ethnicity;
    private String languageMedium;

    private String studentPopulation;
    private String teacherPopulation;
    private int classCount;

    private SchoolStatus status;
}
