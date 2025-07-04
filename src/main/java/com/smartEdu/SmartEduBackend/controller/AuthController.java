package com.smartEdu.SmartEduBackend.controller;

import com.smartEdu.SmartEduBackend.entity.CustomUserDetails;
import com.smartEdu.SmartEduBackend.service.CustomUserDetailsService;
import com.smartEdu.SmartEduBackend.util.AuthRequest;
import com.smartEdu.SmartEduBackend.util.AuthResponse;
import com.smartEdu.SmartEduBackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");        }

        // Load and cast to CustomUserDetails
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(request.getUsername());

        // Generate JWT
        String jwt = jwtUtil.generateToken(userDetails);

        // Return JWT and role info if needed
         return ResponseEntity.ok(new AuthResponse(jwt, userDetails.getUsername(), userDetails.getRole().name()));
    }
}
