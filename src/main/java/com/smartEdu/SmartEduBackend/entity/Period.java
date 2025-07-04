package com.smartEdu.SmartEduBackend.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Period {
    private String subject;
    private String teacherId;
}
