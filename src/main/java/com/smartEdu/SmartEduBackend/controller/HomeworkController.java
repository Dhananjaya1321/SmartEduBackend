package com.smartEdu.SmartEduBackend.controller;

import com.smartEdu.SmartEduBackend.entity.Homeworks;
import com.smartEdu.SmartEduBackend.service.HomeworkService;
import com.smartEdu.SmartEduBackend.util.ExceptionHandler;
import com.smartEdu.SmartEduBackend.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/homeworks")
@CrossOrigin
public class HomeworkController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeworkController.class);

    @Autowired
    private HomeworkService homeworkService;

    @PostMapping
    public ResponseEntity<ResponseUtil> createHomework(@RequestBody Homeworks homework) {
        try {
            Homeworks saved = homeworkService.saveHomework(homework);
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Homework saved successfully", saved));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ExceptionHandler.handleException(e);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<ResponseUtil> updateHomework(@PathVariable String id, @RequestBody Homeworks homework) {
        try {
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Homework updated successfully", homeworkService.update(id,homework)));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("Homework is not exists!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);

            return ExceptionHandler.handleException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseUtil> deleteHomework(@PathVariable String id) {
        try {
            homeworkService.delete(id);
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Homework deleted successfully", null));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseUtil> getHomeworkById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Homework loaded", homeworkService.getHomeworkById(id)));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping
    public ResponseEntity<ResponseUtil> getAllHomeworks() {
        try {
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "All homeworks loaded", homeworkService.getAllHomeworks()));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<ResponseUtil> getHomeworksByClassId(@PathVariable String classId) {
        try {
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Class homeworks loaded", homeworkService.getHomeworksByClassId(classId)));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("Class is not exists!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);

            return ExceptionHandler.handleException(e);
        }
    }
}
