package com.smartEdu.SmartEduBackend.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ExamTimetableEntry {
    private String stream;
    private String subject;
    private String paper;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}
