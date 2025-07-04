package com.smartEdu.SmartEduBackend.controller;

import com.smartEdu.SmartEduBackend.entity.ZonalEducationOffice;
import com.smartEdu.SmartEduBackend.entity.ZonalEducationOfficeRequest;
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
            ZonalEducationOffice office = zmoeService.createWithUser(request);
            return ResponseEntity.ok(new ResponseUtil(HttpStatus.OK, "ZMOE created successfully", office));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("Username is already exists!") ||
                    e.getMessage().equals("Email is already exists!"))
                return com.smartEdu.SmartEduBackend.util.ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);

            return ExceptionHandler.handleException(e);
        }
    }
}
