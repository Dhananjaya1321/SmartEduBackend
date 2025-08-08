package com.smartEdu.SmartEduBackend.entity;


import com.smartEdu.SmartEduBackend.enums.ExamsAndNICApplicationStatus;
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
@Document(collection = "examsAndNICApplication")
public class ExamsAndNICApplication {
    @Id
    private String id;
    private String studentId;
    private ExamsAndNICApplicationStatus status;

    private String nicFrontImageUrl;
    private String nicBackImageUrl;
    private String birthCertificateFrontImageUrl;
    private String birthCertificateBackImageUrl;
}
