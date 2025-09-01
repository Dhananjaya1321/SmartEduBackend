package com.smartEdu.SmartEduBackend.controller;

import com.smartEdu.SmartEduBackend.entity.Event;
import com.smartEdu.SmartEduBackend.entity.ExamResults;
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

import java.util.List;

@RestController
@RequestMapping("/api/results")
@CrossOrigin
public class ExamResultsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExamResultsController.class);

    @Autowired
    private ExamResultsService examResultsService;

    @PostMapping("/exam")
    public ResponseEntity<ResponseUtil> saveExamResults(
            @RequestBody ExamResults examResults,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Exam saved successfully.", examResultsService.saveExamResults(examResults, token))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

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

    @GetMapping("/my-class/to-teachers/{examId}")
    public ResponseEntity<ResponseUtil> getAllClassStudentsResultsDetails(
            @PathVariable String examId,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(
                            HttpStatus.OK,
                            "Events fetched successfully.",
                            examResultsService.getAllClassStudentsResultsDetails(examId,token)
                    )
            );
        } catch (Exception e) {
            LOGGER.error("Error fetching events by grade", e);
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/my-class/student/to-teachers/{studentId}")
    public ResponseEntity<ResponseUtil> getStudentsResultsDetailsToTeacher(
            @PathVariable String studentId,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(
                            HttpStatus.OK,
                            "Events fetched successfully.",
                            examResultsService.getStudentsResultsDetailsToTeacher(studentId,token)
                    )
            );
        } catch (Exception e) {
            LOGGER.error("Error fetching events by grade", e);
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/student-report/to-parents")
    public ResponseEntity<ResponseUtil> getStudentsResultsDetailsToParents(
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(
                            HttpStatus.OK,
                            "Events fetched successfully.",
                            examResultsService.getStudentsResultsDetailsToParents(token)
                    )
            );
        } catch (Exception e) {
            LOGGER.error("Error fetching events by grade", e);
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/student-class/to-parents")
    public ResponseEntity<ResponseUtil> getAllClassStudentsResultsDetailsToParents(
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(
                            HttpStatus.OK,
                            "Events fetched successfully.",
                            examResultsService.getAllClassStudentsResultsDetailsToParents(token)
                    )
            );
        } catch (Exception e) {
            LOGGER.error("Error fetching events by grade", e);
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/student-class/my-child/to-parents/{examId}")
    public ResponseEntity<ResponseUtil> getMyChildData(
            @PathVariable String examId,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(
                            HttpStatus.OK,
                            "Events fetched successfully.",
                            examResultsService.getMyChildData(examId,token)
                    )
            );
        } catch (Exception e) {
            LOGGER.error("Error fetching events by grade", e);
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/national-level-exams-results/to-parents/{indexNumber}/{examName}/{year}")
    public ResponseEntity<ResponseUtil> getNationalLevelExamsResults(
            @PathVariable String indexNumber,
            @PathVariable String examName,
            @PathVariable String year
    ) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(
                            HttpStatus.OK,
                            "Events fetched successfully.",
                            examResultsService.getNationalLevelExamsResults(indexNumber,examName,year)
                    )
            );
        } catch (Exception e) {
            LOGGER.error("Error fetching events by grade", e);
            return ExceptionHandler.handleException(e);
        }
    }
}
