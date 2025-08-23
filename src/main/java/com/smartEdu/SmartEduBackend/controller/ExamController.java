package com.smartEdu.SmartEduBackend.controller;

import com.smartEdu.SmartEduBackend.entity.Exam;
import com.smartEdu.SmartEduBackend.service.ExamService;
import com.smartEdu.SmartEduBackend.util.ExceptionHandler;
import com.smartEdu.SmartEduBackend.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exams")
@CrossOrigin
public class ExamController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExamController.class);

    @Autowired
    private ExamService examService;

    @PostMapping
    public ResponseEntity<ResponseUtil> save(
            @RequestBody Exam exam,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Exam saved successfully.", examService.save(exam, token))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseUtil> update(@PathVariable String id, @RequestBody Exam exam) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Exam updated successfully.", examService.update(id, exam))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseUtil> delete(@PathVariable String id) {
        try {
            examService.delete(id);
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Exam deleted successfully.", null));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseUtil> getById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Exam retrieved successfully.", examService.findById(id).orElse(null))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping
    public ResponseEntity<ResponseUtil> getAll(@RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(
                            HttpStatus.OK,
                            "Exams retrieved successfully.",
                            examService.findAll(token)
                    )
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/grade")
    public ResponseEntity<ResponseUtil> getByGrade(
            @RequestParam String grade,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(
                            HttpStatus.OK,
                            "Exams by grade retrieved successfully.",
                            examService.findByGrade(grade,token)
                    )
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/grade/term-exams/to-parents")
    public ResponseEntity<ResponseUtil> getByGradeTermExamsToParents(
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(
                            HttpStatus.OK,
                            "Exams by grade retrieved successfully.",
                            examService.getByGradeTermExamsToParents((token))
                    )
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }
    @GetMapping("/grade/al-exams/to-parents")
    public ResponseEntity<ResponseUtil> getByGradeALExamsToParents() {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(
                            HttpStatus.OK,
                            "Exams by grade retrieved successfully.",
                            examService.getByGradeALExamsToParents()
                    )
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

  @GetMapping("/grade/ol-exams/to-parents")
    public ResponseEntity<ResponseUtil> getByGradeOLExamsToParents() {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(
                            HttpStatus.OK,
                            "Exams by grade retrieved successfully.",
                            examService.getByGradeOLExamsToParents()
                    )
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/grade/g5-exams/to-parents")
    public ResponseEntity<ResponseUtil> getByGradeG5ExamsToParents() {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(
                            HttpStatus.OK,
                            "Exams by grade retrieved successfully.",
                            examService.getByGradeG5ExamsToParents()
                    )
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/year")
    public ResponseEntity<ResponseUtil> getByYear(@RequestParam String year) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Exams by year retrieved successfully.", examService.findByYear(year))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }
}
