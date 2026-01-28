package com.rydlo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfiguration {

    private final CustomJwtVerificationFilter jwtFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        log.info("Configuring Spring Security for Rydlo");

        http
     // Stateless REST API
        .cors(org.springframework.security.config.Customizer.withDefaults())
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        // Handle 403 Forbidden errors
        .exceptionHandling(ex -> ex
            .accessDeniedHandler(accessDeniedHandler)
        )

        .authorizeHttpRequests(auth -> auth

            // ===== PUBLIC =====
            .requestMatchers(
                "/auth/**",
                "/auth/login",
                "/auth/forgot-password",
                "/auth/verify-otp",
                "/auth/reset-password",
                "/users/register",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html"
            ).permitAll()

            // CORS preflight
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            // ===== BIKES =====
            // Anyone can view/search bikes
            .requestMatchers(HttpMethod.GET, "/bikes/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/bikes/available").permitAll()
            .requestMatchers(HttpMethod.POST, "/bikes/*/price-preview").permitAll()

            // Only OWNER can create/update bikes
            .requestMatchers(HttpMethod.POST, "/bikes/**").hasAuthority("ROLE_OWNER")
            .requestMatchers(HttpMethod.PUT, "/bikes/**").hasAuthority("ROLE_OWNER")
            .requestMatchers(HttpMethod.PATCH, "/bikes/**").hasAuthority("ROLE_OWNER")
            .requestMatchers(HttpMethod.DELETE, "/bikes/**").hasAuthority("ROLE_OWNER")
            
            // ===== BOOKINGS =====
            
            // ===== BOOKINGS =====
            
            .requestMatchers(HttpMethod.POST, "/bookings/{bookingId}/cancel").access((authentication, context) -> 
                new org.springframework.security.authorization.AuthorizationDecision(
                    authentication.get().getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER") || a.getAuthority().equals("ROLE_OWNER"))
                )
            )
            
            // ======Transactions======

            .requestMatchers(HttpMethod.GET, "/transactions/my").hasAuthority("ROLE_CUSTOMER")
            // ===== CUSTOMER =====
            .requestMatchers("/customers/**")
            .hasAuthority("ROLE_CUSTOMER")

            // ===== OWNER =====
            .requestMatchers("/owners/**")
            .hasAuthority("ROLE_OWNER")

            // ===== BOOKINGS =====
            .requestMatchers("/bookings/**")
            .authenticated()

            // ===== ADMIN =====
            .requestMatchers("/admin/**")
            .hasAuthority("ROLE_ADMIN")

            // ===== EVERYTHING ELSE =====
            .anyRequest().authenticated()
        )

        // JWT verification
        .addFilterBefore(
            jwtFilter,
            UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    // Used during login (AuthController)
    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Password hashing
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOrigins(java.util.List.of("http://localhost:5173"));
        configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
