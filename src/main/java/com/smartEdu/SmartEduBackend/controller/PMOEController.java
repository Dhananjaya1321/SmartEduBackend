package com.smartEdu.SmartEduBackend.controller;

import com.smartEdu.SmartEduBackend.entity.ProvincialEducationOffice;
import com.smartEdu.SmartEduBackend.entity.ProvincialEducationOfficeRequest;
import com.smartEdu.SmartEduBackend.entity.Teacher;
import com.smartEdu.SmartEduBackend.service.PMOEService;
import com.smartEdu.SmartEduBackend.util.ExceptionHandler;
import com.smartEdu.SmartEduBackend.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pmoe")
@RequiredArgsConstructor
@CrossOrigin
public class PMOEController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PMOEController.class);

    private final PMOEService PMOEService;

    @PostMapping
    public ResponseEntity<ResponseUtil> createPMOE(@RequestBody ProvincialEducationOfficeRequest request) {
        try {
            ProvincialEducationOffice office = PMOEService.createProvincialEducationOfficeWithUser(request);
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "PMOE created successfully", office));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("Username is already exists!") ||
                    e.getMessage().equals("Email is already exists!"))
                return com.smartEdu.SmartEduBackend.util.ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);

            return ExceptionHandler.handleException(e);
        }
    }

    @PutMapping("/{id}")
    private ResponseEntity<ResponseUtil> updatePMOE(@PathVariable String id, @RequestBody ProvincialEducationOffice office) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Office updated successfully.", PMOEService.updatePMOE(id, office))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("Office not found!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/admins")
    private ResponseEntity<ResponseUtil> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Schools retrieved successfully.",PMOEService.findAll(page, size))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/province")
    private ResponseEntity<ResponseUtil> getLoggedInProvince(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Schools retrieved successfully.",PMOEService.getLoggedInProvince(token))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }
}
