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
public class Report {
    private String year;
    private String examId;
    private String examName;
    private String gradeId;
    private String gradeName;

    private double totalMarks;
    private double averageMarks;
    private int rank;


    private List<Marks> marksList = new ArrayList<>();
}
