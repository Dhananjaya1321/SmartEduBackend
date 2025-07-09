package com.smartEdu.SmartEduBackend.controller;

import com.smartEdu.SmartEduBackend.entity.*;
import com.smartEdu.SmartEduBackend.service.PMOEService;
import com.smartEdu.SmartEduBackend.service.ZMOEService;
import com.smartEdu.SmartEduBackend.util.ExceptionHandler;
import com.smartEdu.SmartEduBackend.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/zmoe")
@RequiredArgsConstructor
@CrossOrigin
public class ZMOEController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZMOEController.class);

    private final ZMOEService zmoeService;

    @PostMapping
    public ResponseEntity<ResponseUtil> createZMOE(@RequestBody ZonalEducationOfficeRequest request) {
        try {
            ZonalEducationOffice office = zmoeService.createZonalEducationOfficeWithUser(request);
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "ZMOE created successfully", office));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("Username is already exists!") ||
                    e.getMessage().equals("Email is already exists!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);

            return ExceptionHandler.handleException(e);
        }
    }

    @PostMapping("/create-new-admin/{id}")
    public ResponseEntity<ResponseUtil> createNewAdminForZonalEducationOffice(@PathVariable String id, @RequestBody ZonalEducationOfficeRequest request) {
        try {
            User user = zmoeService.createNewAdminForZonalEducationOffice(id,request);
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "Admin created successfully", user));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("Username is already exists!") ||
                    e.getMessage().equals("Email is already exists!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);

            return ExceptionHandler.handleException(e);
        }
    }

    @PutMapping("/{id}")
    private ResponseEntity<ResponseUtil> updateZMOE(@PathVariable String id, @RequestBody ZonalEducationOffice office) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Office updated successfully.", zmoeService.updateZMOE(id, office))
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
                    new ResponseUtil(HttpStatus.OK, "Schools retrieved successfully.", zmoeService.findAll(page, size))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }
}
