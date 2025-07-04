package com.smartEdu.SmartEduBackend.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Period {
    private int periodNumber;
    private String subject;
    private String teacherId;
}
