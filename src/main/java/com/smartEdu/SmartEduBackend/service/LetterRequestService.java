package com.smartEdu.SmartEduBackend.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.smartEdu.SmartEduBackend.entity.LetterRequest;
import com.smartEdu.SmartEduBackend.entity.Parent;
import com.smartEdu.SmartEduBackend.entity.Student;
import com.smartEdu.SmartEduBackend.entity.User;
import com.smartEdu.SmartEduBackend.enums.LetterStatus;
import com.smartEdu.SmartEduBackend.repo.*;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LetterRequestService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ParentRepo parentRepo;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private GradesRepo gradesRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private LetterRequestRepo letterRequestRepo;

    public LetterRequest create(LetterRequest request, String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username).get();
        String profileId = user.getProfileId();

        Parent parent = parentRepo.findById(profileId).get();
        Student student = studentRepo.findById(parent.getStudentIds().getFirst()).get();

        request.setStudentId(student.getId());
        request.setStudentName(student.getFullNameWithInitials());
        request.setLastGrade(gradesRepo.findById(student.getGradeId()).get().getGradeName());
        request.setStatus(LetterStatus.PENDING);
        request.setRequestedDate(LocalDate.now());
        return letterRequestRepo.save(request);
    }


    public  List<LetterRequest> getAllAcceptedLettersAndCertificatesToParents(String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username).get();
        String profileId = user.getProfileId();

        Parent parent = parentRepo.findById(profileId).get();
        Student student = studentRepo.findById(parent.getStudentIds().getFirst()).get();
        return letterRequestRepo.findByStudentIdAndStatus(student.getId(), LetterStatus.APPROVED);
    }

    public  List<LetterRequest> getPendingLettersAndCertificatesToParents(String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username).get();
        String profileId = user.getProfileId();

        Parent parent = parentRepo.findById(profileId).get();
        Student student = studentRepo.findById(parent.getStudentIds().getFirst()).get();
        return letterRequestRepo.findByStudentIdAndStatus(student.getId(), LetterStatus.PENDING);
    }

    public  List<LetterRequest> getRejectLettersAndCertificatesToParents(String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepo.findByUsername(username).get();
        String profileId = user.getProfileId();

        Parent parent = parentRepo.findById(profileId).get();
        Student student = studentRepo.findById(parent.getStudentIds().getFirst()).get();
        return letterRequestRepo.findByStudentIdAndStatus(student.getId(), LetterStatus.REJECTED);
    }


    public LetterRequest approve(String id, String signatureUrl, String documentUrl, String remarks) {
        LetterRequest request = letterRequestRepo.findById(id).orElseThrow(() -> new RuntimeException("Letter not found"));
        request.setStatus(LetterStatus.APPROVED);
        request.setPrincipalSignatureUrl(signatureUrl);
        request.setDocumentUrl(documentUrl);
        request.setIssuedDate(LocalDate.now());
        request.setPrincipalRemarks(remarks);
        return letterRequestRepo.save(request);
    }

    public LetterRequest reject(String id, String remarks) {
        LetterRequest request = letterRequestRepo.findById(id).orElseThrow(() -> new RuntimeException("Letter not found"));
        request.setStatus(LetterStatus.REJECTED);
        request.setPrincipalRemarks(remarks);
        return letterRequestRepo.save(request);
    }

    public List<LetterRequest> getByStudentId(String studentId) {
        return letterRequestRepo.findByStudentId(studentId);
    }

    public List<LetterRequest> getByStatus(LetterStatus status) {
        return letterRequestRepo.findByStatus(status);
    }

    public Optional<LetterRequest> getById(String id) {
        return letterRequestRepo.findById(id);
    }

    public LetterRequest uploadCertificate(String studentId, String requestId, MultipartFile pdfFile, MultipartFile signatureFile) {
        try {
            // Validate inputs
            if (pdfFile == null || pdfFile.isEmpty()) {
                throw new IllegalArgumentException("PDF file is required");
            }

            // Find the letter request
            LetterRequest request = letterRequestRepo.findById(requestId)
                    .orElseThrow(() -> new RuntimeException("Letter request not found"));
            if (!request.getStudentId().equals(studentId)) {
                throw new IllegalArgumentException("Student ID does not match the letter request");
            }

            // Initialize GridFS for smartedu database
            GridFSBucket gridFSBucket = GridFSBuckets.create(mongoTemplate.getDb());

            // Save PDF to GridFS
            GridFSUploadOptions pdfOptions = new GridFSUploadOptions()
                    .metadata(new Document("type", "pdf"));
            ObjectId pdfFileId = gridFSBucket.uploadFromStream(
                    pdfFile.getOriginalFilename(),
                    pdfFile.getInputStream(),
                    pdfOptions
            );

            // Save signature to GridFS (if provided)
            String signatureFileId = null;
            if (signatureFile != null && !signatureFile.isEmpty()) {
                GridFSUploadOptions signatureOptions = new GridFSUploadOptions()
                        .metadata(new Document("type", "image"));
                signatureFileId = gridFSBucket.uploadFromStream(
                        signatureFile.getOriginalFilename(),
                        signatureFile.getInputStream(),
                        signatureOptions
                ).toString();
            }

            // Update LetterRequest with file IDs and status
            request.setDocumentUrl(pdfFileId.toString());
            request.setPrincipalSignatureUrl(signatureFileId);
            request.setStatus(LetterStatus.APPROVED);
            request.setIssuedDate(LocalDate.now());
            request.setPrincipalRemarks("Certificate uploaded successfully");

            return letterRequestRepo.save(request);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload files to GridFS: " + e.getMessage());
        }
    }
}
