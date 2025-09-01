package com.smartEdu.SmartEduBackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/user/**").permitAll()
                        .requestMatchers("/api/exams/**").permitAll()
                        .requestMatchers("/api/parents/**").hasAnyRole("PARENT")
                        .requestMatchers("/api/letters/**").hasAnyRole("PARENT","SCHOOL_ADMIN")
                        .requestMatchers("/api/principals/**").hasAnyRole("PARENT","ZMOE_ADMIN","ZMOE_EMPLOYEE")
                        .requestMatchers("/api/attendance/**").hasAnyRole("PARENT","TEACHER","SCHOOL_ADMIN","SCHOOL_EMPLOYEE")
                        .requestMatchers("/api/classes/**").hasAnyRole("PARENT","TEACHER","SCHOOL_ADMIN","SCHOOL_EMPLOYEE")
                        .requestMatchers("/api/timetables/**").hasAnyRole("PARENT","TEACHER","SCHOOL_ADMIN","SCHOOL_EMPLOYEE")
                        .requestMatchers("/api/events/**").hasAnyRole("PARENT","TEACHER","SCHOOL_ADMIN","SCHOOL_EMPLOYEE")
                        .requestMatchers("/api/students/**").hasAnyRole("PARENT","TEACHER","SCHOOL_ADMIN","SCHOOL_EMPLOYEE")
                        .requestMatchers("/api/teachers/**").hasAnyRole("PARENT","TEACHER","SCHOOL_ADMIN","SCHOOL_EMPLOYEE","ZMOE_ADMIN","ZMOE_EMPLOYEE")
                        .requestMatchers("/api/schools/**").hasAnyRole("PARENT","TEACHER","SCHOOL_ADMIN","SCHOOL_EMPLOYEE","ZMOE_ADMIN","ZMOE_EMPLOYEE")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
