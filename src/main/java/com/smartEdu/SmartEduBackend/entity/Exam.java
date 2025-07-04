package com.smartEdu.SmartEduBackend.entity;


import com.smartEdu.SmartEduBackend.enums.ExamLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "exams")
public class Exam {
    @Id
    private String id;

    private String examName;
    private String grade;
    private int year;

    private ExamLevel level; //school level, zonal level, province level or national level

    private List<ExamTimetableEntry> timetable = new ArrayList<>();
}
