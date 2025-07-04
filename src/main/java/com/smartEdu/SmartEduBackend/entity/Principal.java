package com.smartEdu.SmartEduBackend.entity;


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
@Document(collection = "principals")
public class Principal {
    @Id
    private String id;
    private String schoolId;

    private String fullName;
    private String nic;
    private String address;
    private String contact;
    private String moeId;

    private String nicFrontImageUrl;
    private String nicBackImageUrl;
    private String moeIdFrontImageUrl;
    private String moeIdBackImageUrl;
    private String appointmentLetterUrl;
}
