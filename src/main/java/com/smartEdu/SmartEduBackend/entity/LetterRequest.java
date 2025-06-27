package com.smartEdu.SmartEduBackend.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "letters")
public class LetterRequest {
    @Id
    private String id;

    private String studentId;
    private String studentName;
    private String letterType; // Enum: LEAVING_CERTIFICATE, CHARACTER_CERTIFICATE, etc.
    private String lastGrade;

    private String description;
    private LocalDate requestedDate;
    private LocalDate issuedDate;

    private String status; // PENDING, APPROVED, REJECTED
    private String principalRemarks;

    private String principalSignatureUrl;
    private String documentUrl;
}
