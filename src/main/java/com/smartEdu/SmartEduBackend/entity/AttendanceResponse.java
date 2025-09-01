package com.smartEdu.SmartEduBackend.entity;

import com.smartEdu.SmartEduBackend.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceResponse {
    private String studentId;
    private String studentName;
    private int totalAttended;
    private int totalAbsent;
    private int totalDays;
    private double attendedRate;
    private AttendanceStatus status;
}
