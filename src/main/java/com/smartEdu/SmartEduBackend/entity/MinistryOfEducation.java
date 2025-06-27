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
@Document(collection = "ministryOfEducation")
public class MinistryOfEducation {
    @Id
    private String id;

    private String name = "Ministry of Education";
    private List<ProvincialMinistryOfEducation> provincialOffices = new ArrayList<>();
}
