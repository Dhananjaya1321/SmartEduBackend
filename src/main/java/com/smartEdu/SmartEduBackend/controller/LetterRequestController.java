package com.smartEdu.SmartEduBackend.controller;

import com.smartEdu.SmartEduBackend.entity.LetterRequest;
import com.smartEdu.SmartEduBackend.enums.LetterStatus;
import com.smartEdu.SmartEduBackend.service.LetterRequestService;
import com.smartEdu.SmartEduBackend.util.ResponseUtil;
import com.smartEdu.SmartEduBackend.util.ExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/letters")
@CrossOrigin
public class LetterRequestController {

    @Autowired
    private LetterRequestService service;

    @PostMapping
    public ResponseEntity<ResponseUtil> createRequest(
            @RequestBody LetterRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(
                            HttpStatus.CREATED,
                            "Letter request submitted successfully.",
                            service.create(request,token)
                    )
            );
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @PostMapping("/save-pdf/{studentId}/{requestId}")
    public ResponseEntity<ResponseUtil> uploadCertificate(
            @PathVariable String studentId,
            @PathVariable String requestId,
            @RequestParam("file") MultipartFile pdfFile,
            @RequestParam(value = "signature", required = false) MultipartFile signatureFile
    ) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Letter approved successfully.",
                            service.uploadCertificate(studentId, requestId, pdfFile, signatureFile))
            );
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<ResponseUtil> approveRequest(
            @PathVariable String id,
            @RequestParam String signatureUrl,
            @RequestParam String documentUrl,
            @RequestParam(required = false) String principalRemarks
    ) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Letter approved successfully.",
                            service.approve(id, signatureUrl, documentUrl, principalRemarks))
            );
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<ResponseUtil> rejectRequest(
            @PathVariable String id,
            @RequestParam String principalRemarks
    ) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Letter rejected successfully.",
                            service.reject(id, principalRemarks))
            );
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/accepted/to-parents")
    public ResponseEntity<ResponseUtil> getAllAcceptedLettersAndCertificatesToParents(
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Student letters loaded.", service.getAllAcceptedLettersAndCertificatesToParents(token))
            );
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ResponseUtil> getByStudent(@PathVariable String studentId) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Student letters loaded.", service.getByStudentId(studentId))
            );
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<ResponseUtil> getPendingRequests() {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Pending letters loaded.", service.getByStatus(LetterStatus.PENDING))
            );
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseUtil> getById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Letter loaded.", service.getById(id).orElse(null))
            );
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }
}
