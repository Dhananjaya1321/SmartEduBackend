package com.smartEdu.SmartEduBackend.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GradesResponse {
    @Id
    private String id;
    private String schoolId;
    private int gradeName;
    private List<String> streamsOfALs;


    private List<ClassRoom> classRooms;
}
