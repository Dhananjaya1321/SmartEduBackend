package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.LetterRequest;
import com.smartEdu.SmartEduBackend.entity.Parent;
import com.smartEdu.SmartEduBackend.entity.Student;
import com.smartEdu.SmartEduBackend.entity.User;
import com.smartEdu.SmartEduBackend.enums.LetterStatus;
import com.smartEdu.SmartEduBackend.repo.*;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
