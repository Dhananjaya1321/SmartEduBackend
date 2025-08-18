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
public class AttendanceStudentIdAndStatus {
    private String studentId;
    private AttendanceStatus status;
}
