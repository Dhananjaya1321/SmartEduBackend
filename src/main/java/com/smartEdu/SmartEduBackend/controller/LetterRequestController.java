package com.smartEdu.SmartEduBackend.controller;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import com.smartEdu.SmartEduBackend.entity.LetterRequest;
import com.smartEdu.SmartEduBackend.enums.LetterStatus;
import com.smartEdu.SmartEduBackend.service.LetterRequestService;
import com.smartEdu.SmartEduBackend.util.ResponseUtil;
import com.smartEdu.SmartEduBackend.util.ExceptionHandler;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;


@RestController
@RequestMapping("/api/letters")
@CrossOrigin
public class LetterRequestController {
    @Autowired
    private MongoTemplate mongoTemplate;

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

    @GetMapping("/files/{id}")
    public ResponseEntity<Resource> getFile(@PathVariable String id) {
        GridFSBucket gridFSBucket = GridFSBuckets.create(mongoTemplate.getDb());
        ObjectId fileId;
        try {
            fileId = new ObjectId(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        GridFSFile gridFSFile = gridFSBucket.find(Filters.eq("_id", fileId)).first();
        if (gridFSFile == null) {
            return ResponseEntity.notFound().build();
        }

        GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(fileId);
        InputStreamResource resource = new InputStreamResource(downloadStream);

        return ResponseEntity.ok()
                .contentLength(gridFSFile.getLength())
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + gridFSFile.getFilename() + "\"")
                .body(resource);
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
