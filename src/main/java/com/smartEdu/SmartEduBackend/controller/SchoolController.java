package com.smartEdu.SmartEduBackend.controller;

import com.smartEdu.SmartEduBackend.entity.School;
import com.smartEdu.SmartEduBackend.entity.SchoolRequest;
import com.smartEdu.SmartEduBackend.enums.SchoolStatus;
import com.smartEdu.SmartEduBackend.service.SchoolService;
import com.smartEdu.SmartEduBackend.util.ExceptionHandler;
import com.smartEdu.SmartEduBackend.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/schools")
@CrossOrigin
public class SchoolController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchoolController.class);

    @Autowired
    private SchoolService service;

    @PostMapping
    private ResponseEntity<ResponseUtil> save(@RequestBody SchoolRequest request) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "School and Principal saved successfully.",
                            service.saveSchoolWithPrincipal(request))
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
    private ResponseEntity<ResponseUtil> update(@PathVariable String id, @RequestBody School school) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "School updated successfully.", service.update(id, school))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("School not found!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);
            return ExceptionHandler.handleException(e);
        }
    }

    @PutMapping("/update-school-status/{id}")
    private ResponseEntity<ResponseUtil> updateSchoolStatus(@PathVariable String id, @Param("status") SchoolStatus status) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "School updated successfully.", service.updateSchoolStatus(id, status))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("School not found!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);
            return ExceptionHandler.handleException(e);
        }
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<ResponseUtil> delete(@PathVariable String id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "School deleted successfully.", null)
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("School not found!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/{id}")
    private ResponseEntity<ResponseUtil> findById(@PathVariable String id) {
        try {
            Optional<School> school = service.findById(id);
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "School retrieved successfully.", school.orElse(null))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping
    private ResponseEntity<ResponseUtil> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Schools retrieved successfully.", service.findAll(page, size))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }
}
