package com.smartEdu.SmartEduBackend.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseUtil {
    private HttpStatus state;
    private String message;
    private Object data;

    public ResponseUtil(HttpStatus state, String message) {
        this.state = state;
        this.message = message;
    }
}

