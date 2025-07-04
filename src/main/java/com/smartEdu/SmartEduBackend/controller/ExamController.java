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
    public ResponseEntity<ResponseUtil> save(@RequestBody Exam exam) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Exam saved successfully.", examService.save(exam))
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
    public ResponseEntity<ResponseUtil> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Page<Exam> exams = examService.findAll(page, size);
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Exams retrieved successfully.", exams.getContent()));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/grade")
    public ResponseEntity<ResponseUtil> getByGrade(@RequestParam String grade) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Exams by grade retrieved successfully.", examService.findByGrade(grade))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/year")
    public ResponseEntity<ResponseUtil> getByYear(@RequestParam int year) {
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
