// AchievementController.java
package com.smartEdu.SmartEduBackend.controller;

import com.smartEdu.SmartEduBackend.entity.Achievements;
import com.smartEdu.SmartEduBackend.service.AchievementService;
import com.smartEdu.SmartEduBackend.util.ResponseUtil;
import com.smartEdu.SmartEduBackend.util.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
@CrossOrigin
public class AchievementController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AchievementController.class);

    @Autowired
    private AchievementService achievementService;

    @PostMapping("/student/{studentId}")
    public ResponseEntity<ResponseUtil> createAchievement(@PathVariable String studentId, @RequestBody Achievements achievement) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.CREATED, "Achievement saved successfully", achievementService.save(studentId, achievement))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("Student is not exists!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);

            return ExceptionHandler.handleException(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseUtil> updateAchievement(@PathVariable String id, @RequestBody Achievements achievement) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Achievement updated successfully", achievementService.update(id, achievement))
            );
        } catch (Exception e) {
            if (e.getMessage().equals("Achievement is not exists!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);

            return ExceptionHandler.handleException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseUtil> deleteAchievement(@PathVariable String id) {
        try {
            achievementService.delete(id);
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Achievement deleted", null));
        } catch (Exception e) {
            if (e.getMessage().equals("Achievement is not exists!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);

            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ResponseUtil> getAchievementsByStudent(@PathVariable String studentId) {
        try {
            List<Achievements> achievements = achievementService.getAchievementsByStudentId(studentId);
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Achievements loaded", achievements));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("Student is not exists!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);

            return ExceptionHandler.handleException(e);
        }
    }
}
