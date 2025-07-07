package com.smartEdu.SmartEduBackend.controller;

import com.smartEdu.SmartEduBackend.util.ExceptionHandler;
import com.smartEdu.SmartEduBackend.entity.User;
import com.smartEdu.SmartEduBackend.service.UserService;
import com.smartEdu.SmartEduBackend.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService service;

    @PostMapping
    private ResponseEntity<ResponseUtil> save(
            @RequestBody User user, @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(
                            HttpStatus.OK,
                            "User saved successfully.",
                            service.save(user,token)
                    )
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("User is not exists!") ||
                    e.getMessage().equals("Username is already exists!") ||
                    e.getMessage().equals("Email is already exists!"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);

            return ExceptionHandler.handleException(e);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MOE_ADMIN', 'PMOE_ADMIN', 'ZMOE_ADMIN', 'SCHOOL_ADMIN')")
    private ResponseEntity<ResponseUtil> update(
            @PathVariable String id,
            @RequestBody User user
    ) {
        try {
            return ResponseEntity.ok(
                    new ResponseUtil(
                            HttpStatus.OK,
                            "User updated successfully.",
                            service.update(id,user)
                    )
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("User is not exists!") ||
                    e.getMessage().equals("Unauthorized to update users"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);

            return ExceptionHandler.handleException(e);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MOE_ADMIN', 'PMOE_ADMIN', 'ZMOE_ADMIN', 'SCHOOL_ADMIN')")
    private ResponseEntity<ResponseUtil> delete(
            @PathVariable String id
    ) {
        try {
            service.delete(id);
            return ResponseEntity.ok(
                    new ResponseUtil(
                            HttpStatus.OK,
                            "User deleted successfully.",
                            null
                    )
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("User is not exists!") ||
                    e.getMessage().equals("Unauthorized to delete user"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);

            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MOE_ADMIN', 'PMOE_ADMIN', 'ZMOE_ADMIN', 'SCHOOL_ADMIN')")
    private ResponseEntity<ResponseUtil> getAll(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(
                    new ResponseUtil(
                            HttpStatus.OK,
                            "Users retrieved successfully.",
                            service.findAllByRole(token)
                    )
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/check-email-and-send-otp")
    public ResponseEntity<ResponseUtil> checkEmailAndSendOTP(@RequestParam String email) {
        try {
            String otp = service.checkEmailAndSendOTP(email);

            return ResponseEntity.ok(
                    new ResponseUtil(
                            HttpStatus.OK,
                            "OTP sent successfully.",
                            otp
                    )
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("Incorrect email"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);

            return ExceptionHandler.handleException(e);        }
    }

    @PutMapping("/update-password")
    public ResponseEntity<ResponseUtil> updatePassword(
            @RequestParam String email,
            @RequestParam String newPassword
    ) {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            System.out.println(principal.toString());
            return ResponseEntity.ok(
                    new ResponseUtil(
                            HttpStatus.OK,
                            "Password updated successfully",
                            service.updatePassword(email, newPassword)
                    )
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (e.getMessage().equals("User not found with given email"))
                return ExceptionHandler.handleCustomException(HttpStatus.NOT_FOUND, e);

            return ExceptionHandler.handleException(e);
        }
    }

}
