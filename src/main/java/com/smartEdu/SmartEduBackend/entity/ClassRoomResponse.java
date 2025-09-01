package com.smartEdu.SmartEduBackend.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ClassRoomResponse {
    @Id
    private String id;

    private String className;
    private String gradeId;
    private String classTeacherId;
    private String classTeacherName;
    private String classTeacherSubject;

    private ClassTimetable timetable;
    private List<String> studentIds;
}
