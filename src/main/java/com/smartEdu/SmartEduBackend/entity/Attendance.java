package com.smartEdu.SmartEduBackend.entity;

import com.smartEdu.SmartEduBackend.enums.AttendanceStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "attendances")
public class Attendance {

    @Id
    private String id;

    private String studentId;
    private String classId;
    private LocalDate date;
    private boolean present;
}
