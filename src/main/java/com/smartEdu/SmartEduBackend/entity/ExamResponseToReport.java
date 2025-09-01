package com.smartEdu.SmartEduBackend.entity;


import com.smartEdu.SmartEduBackend.enums.ExamLevel;
import com.smartEdu.SmartEduBackend.enums.ExamsResults;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ExamResponseToReport {
    @Id
    private String id;
    private String examName;
    private String institutionId;
    private String grade;
    private String year;
    private ExamLevel level;
    private ExamsResults examsResultsStatus;

    private List<ExamTimetableEntryToReport> timetable = new ArrayList<>();
}
