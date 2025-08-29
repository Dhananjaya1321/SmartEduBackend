package com.smartEdu.SmartEduBackend.controller;

import com.smartEdu.SmartEduBackend.entity.GradesRequest;
import com.smartEdu.SmartEduBackend.service.DashboardService;
import com.smartEdu.SmartEduBackend.service.GradesService;
import com.smartEdu.SmartEduBackend.util.ExceptionHandler;
import com.smartEdu.SmartEduBackend.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private DashboardService service;

    @GetMapping("/to-school")
    private ResponseEntity<ResponseUtil> getSchoolDashboardDetails(
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Schools retrieved successfully.", service.getSchoolDashboardDetails(token))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/to-zonal")
    private ResponseEntity<ResponseUtil> getZonalDashboardDetails(
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Schools retrieved successfully.", service.getZonalDashboardDetails(token))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/to-province")
    private ResponseEntity<ResponseUtil> getProvinceDashboardDetails(
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Schools retrieved successfully.", service.getProvinceDashboardDetails(token))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/to-moe")
    private ResponseEntity<ResponseUtil> getDetailsToMOEDashboard(
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(HttpStatus.OK, "Schools retrieved successfully.", service.getDetailsToMOEDashboard(token))
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

}
