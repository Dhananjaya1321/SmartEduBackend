package com.smartEdu.SmartEduBackend.controller;

import com.smartEdu.SmartEduBackend.entity.Teacher;
import com.smartEdu.SmartEduBackend.entity.TeacherRegisterRequest;
import com.smartEdu.SmartEduBackend.service.TeacherService;
import com.smartEdu.SmartEduBackend.util.ExceptionHandler;
import com.smartEdu.SmartEduBackend.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teachers")
@CrossOrigin
public class TeacherController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeacherController.class);

    @Autowired
    private TeacherService service;

    @PostMapping("/register")
    private ResponseEntity<ResponseUtil> register(@RequestBody TeacherRegisterRequest request) {
        try {
            Teacher saved = service.registerTeacherWithUser(request);
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Teacher and User saved successfully.", saved)
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("Username is already exists!") ||
                    e.getMessage().equals("Email is already exists!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);
            return ExceptionHandler.handleException(e);
        }
    }

    @PutMapping("/{id}")
    private ResponseEntity<ResponseUtil> update(@PathVariable String id, @RequestBody Teacher teacher) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Teacher updated successfully.", service.update(id, teacher))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("Teacher not found!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);
            return ExceptionHandler.handleException(e);
        }
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<ResponseUtil> delete(@PathVariable String id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Teacher deleted successfully.", null)
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("Teacher not found!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/{id}")
    private ResponseEntity<ResponseUtil> findById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Teacher retrieved successfully.", service.findById(id).orElse(null))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/for-zonal-office")
    private ResponseEntity<ResponseUtil> findAllForZonalOffice(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Teachers retrieved successfully.", service.findAllForZonalOffice(page, size,token))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/for-school")
    private ResponseEntity<ResponseUtil> findAllForSchool(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Teachers retrieved successfully.", service.findAllForSchool(page, size,token))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }
}
