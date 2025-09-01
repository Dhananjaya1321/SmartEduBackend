package com.smartEdu.SmartEduBackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);

        // ✅ Allow Web + Mobile
        config.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",     // React web
                "http://localhost:8081",     // Metro bundler webview
                "http://192.168.*.*:8081",   // RN running on device
                "http://192.168.*.*:19000",  // Expo dev
                "http://192.168.*.*:19006",  // Expo web preview
                "app://-"                    // Custom scheme (mobile app)
        ));

        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
