package com.smartEdu.SmartEduBackend.controller;

import com.smartEdu.SmartEduBackend.entity.ProvincialEducationOffice;
import com.smartEdu.SmartEduBackend.entity.ProvincialEducationOfficeRequest;
import com.smartEdu.SmartEduBackend.entity.User;
import com.smartEdu.SmartEduBackend.service.MOEService;
import com.smartEdu.SmartEduBackend.service.UserService;
import com.smartEdu.SmartEduBackend.util.ExceptionHandler;
import com.smartEdu.SmartEduBackend.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/moe")
@CrossOrigin
public class MOEController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MOEController.class);

    @Autowired
    private MOEService service;

    @PostMapping
    private ResponseEntity<ResponseUtil> save(
            @RequestBody ProvincialEducationOfficeRequest office
    ) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "User saved successfully.", service.createWithUser(office))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("Username is already exists!") ||
                    e.getMessage().equals("Email is already exists!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);

            return ExceptionHandler.handleException(e);
        }
    }


}
