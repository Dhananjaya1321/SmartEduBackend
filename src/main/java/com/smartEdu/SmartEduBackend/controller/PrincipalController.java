package com.smartEdu.SmartEduBackend.controller;

import com.smartEdu.SmartEduBackend.entity.Principal;
import com.smartEdu.SmartEduBackend.service.PrincipalService;
import com.smartEdu.SmartEduBackend.util.ExceptionHandler;
import com.smartEdu.SmartEduBackend.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/principals")
@CrossOrigin
public class PrincipalController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrincipalController.class);

    @Autowired
    private PrincipalService service;

    @PostMapping
    private ResponseEntity<ResponseUtil> save(@RequestBody Principal principal) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Principal saved successfully.", service.save(principal))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @PutMapping("/{id}")
    private ResponseEntity<ResponseUtil> update(@PathVariable String id, @RequestBody Principal principal) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Principal updated successfully.", service.update(id, principal))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("Principal not found!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);
            return ExceptionHandler.handleException(e);
        }
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<ResponseUtil> delete(@PathVariable String id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Principal deleted successfully.", null)
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("Principal not found!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/{id}")
    private ResponseEntity<ResponseUtil> findById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Principal retrieved successfully.", service.findById(id).orElse(null))
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
                    new ResponseUtil(HttpStatus.OK, "Principals retrieved successfully.", service.findAll(page, size))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }
}
