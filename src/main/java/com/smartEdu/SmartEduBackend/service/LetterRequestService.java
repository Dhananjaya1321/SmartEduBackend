package com.smartEdu.SmartEduBackend.service;

import com.smartEdu.SmartEduBackend.entity.LetterRequest;
import com.smartEdu.SmartEduBackend.enums.LetterStatus;
import com.smartEdu.SmartEduBackend.repo.LetterRequestRepo;
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
    private LetterRequestRepo repo;

    public LetterRequest create(LetterRequest request) {
        request.setStatus(LetterStatus.PENDING);
        request.setRequestedDate(LocalDate.now());
        return repo.save(request);
    }

    public LetterRequest approve(String id, String signatureUrl, String documentUrl, String remarks) {
        LetterRequest request = repo.findById(id).orElseThrow(() -> new RuntimeException("Letter not found"));
        request.setStatus(LetterStatus.APPROVED);
        request.setPrincipalSignatureUrl(signatureUrl);
        request.setDocumentUrl(documentUrl);
        request.setIssuedDate(LocalDate.now());
        request.setPrincipalRemarks(remarks);
        return repo.save(request);
    }

    public LetterRequest reject(String id, String remarks) {
        LetterRequest request = repo.findById(id).orElseThrow(() -> new RuntimeException("Letter not found"));
        request.setStatus(LetterStatus.REJECTED);
        request.setPrincipalRemarks(remarks);
        return repo.save(request);
    }

    public List<LetterRequest> getByStudentId(String studentId) {
        return repo.findByStudentId(studentId);
    }

    public List<LetterRequest> getByStatus(LetterStatus status) {
        return repo.findByStatus(status);
    }

    public Optional<LetterRequest> getById(String id) {
        return repo.findById(id);
    }
}
