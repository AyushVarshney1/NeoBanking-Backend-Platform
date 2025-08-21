package com.neobank.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig class
 * --------------------
 * This class configures Spring Security for the application.
 * It's marked as a @Configuration class, which means Spring will automatically detect it
 * and use it to register beans in the application context.
 */
@Configuration
public class SecurityConfig {

    /**
     * Defines the main Spring Security filter chain configuration.
     *
     * @param http - The HttpSecurity object used to configure security rules.
     * @return SecurityFilterChain - the built security filter chain for the app.
     * @throws Exception if there’s a configuration issue.
     *
     * In this setup:
     * - All HTTP requests are permitted without authentication (for now).
     * - CSRF protection is disabled (useful for APIs or development, but dangerous for production without proper safeguards).
     * This is done for Auth Service, because this is not required as this service is not exposed to the internet and the
     * requests come from API Gateway
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Allow all incoming requests without authentication/authorization
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().permitAll())

                // Disable CSRF (Cross-Site Request Forgery) protection (usually needed for browser-based form submissions)
                // For APIs or stateless authentication, CSRF is typically disabled
                .csrf(AbstractHttpConfigurer::disable);

        // Build and return the configured security filter chain
        return http.build();
    }

    /**
     * Defines a bean for BCryptPasswordEncoder.
     *
     * BCryptPasswordEncoder is used to hash passwords before storing them
     * and to verify hashed passwords during authentication.
     *
     * Returning it as a @Bean allows us to inject it anywhere in the application
     * using @Autowired or constructor injection.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}