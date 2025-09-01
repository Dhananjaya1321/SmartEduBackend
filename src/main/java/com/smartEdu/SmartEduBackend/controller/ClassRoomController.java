package com.smartEdu.SmartEduBackend.controller;

import com.smartEdu.SmartEduBackend.entity.ClassRoom;
import com.smartEdu.SmartEduBackend.service.ClassRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.smartEdu.SmartEduBackend.util.ExceptionHandler;
import com.smartEdu.SmartEduBackend.util.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class ClassRoomController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassRoomController.class);

    @Autowired
    private ClassRoomService service;

    @PostMapping
    public ResponseEntity<ResponseUtil> createClass(
            @RequestBody ClassRoom classRoom,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(
                            HttpStatus.OK,
                            "Class saved successfully.",
                            service.createClass(classRoom,token)
                    )
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @PostMapping("/bulk")
    private ResponseEntity<ResponseUtil> saveMultiple(@RequestBody List<ClassRoom> classRooms) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(
                            HttpStatus.OK,
                            "Classes saved successfully.",
                            service.createMultipleClasses(classRooms)
                    )
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @PutMapping("/{id}")
    private ResponseEntity<ResponseUtil> update(@PathVariable String id, @RequestBody ClassRoom classRoom) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Class updated successfully.", service.updateClass(id, classRoom))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("Class not found!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);
            return ExceptionHandler.handleException(e);
        }
    }

    @PutMapping("/classes-reshuffle")
    private ResponseEntity<ResponseUtil> classesReshuffle(
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Classes reshuffle successfully.", service.classesReshuffle(token))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<ResponseUtil> delete(@PathVariable String id) {
        try {
            service.deleteClass(id);
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Class deleted successfully.", null)
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("Class not found!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/{id}")
    private ResponseEntity<ResponseUtil> findById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Class retrieved successfully.", service.getClassById(id))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping
    private ResponseEntity<ResponseUtil> findAll() {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Classes retrieved successfully.", service.getAllClasses())
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/by-grade/{grade}")
    private ResponseEntity<ResponseUtil> findByGrade(@PathVariable String grade) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Classes retrieved successfully.", service.getClassesByGrade(grade))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }
}
