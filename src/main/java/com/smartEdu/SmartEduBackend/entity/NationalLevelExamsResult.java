package com.smartEdu.SmartEduBackend.entity;

import com.smartEdu.SmartEduBackend.enums.ExamsResults;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NationalLevelExamsResult {
    private String subject;
    private String result;
}
