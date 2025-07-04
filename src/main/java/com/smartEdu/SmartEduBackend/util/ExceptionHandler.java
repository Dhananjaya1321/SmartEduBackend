package com.smartEdu.SmartEduBackend.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class ExceptionHandler {
    public static ResponseEntity<ResponseUtil> handleException(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Something went wrong";

        return ResponseEntity.status(status).body(new ResponseUtil(status, message, null));
    }

    public static ResponseEntity<ResponseUtil> handleCustomException(HttpStatus status, Exception e) {
        return ResponseEntity.status(status).body(new ResponseUtil(status, e.getMessage(), null));
    }
}
