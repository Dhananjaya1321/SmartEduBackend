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
    private String gradeName;
    private String stream;


    private List<ClassRoomResponse> classRooms;
}
