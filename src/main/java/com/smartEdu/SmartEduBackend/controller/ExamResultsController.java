package com.smartEdu.SmartEduBackend.controller;

import com.smartEdu.SmartEduBackend.entity.SubjectResults;
import com.smartEdu.SmartEduBackend.service.ExamResultsService;
import com.smartEdu.SmartEduBackend.util.ExceptionHandler;
import com.smartEdu.SmartEduBackend.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/results")
@CrossOrigin
public class ExamResultsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExamResultsController.class);

    @Autowired
    private ExamResultsService examResultsService;

    @PostMapping
    public ResponseEntity<ResponseUtil> save(
            @RequestBody SubjectResults subjectResults,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Exam saved successfully.", examResultsService.save(subjectResults, token))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

}
