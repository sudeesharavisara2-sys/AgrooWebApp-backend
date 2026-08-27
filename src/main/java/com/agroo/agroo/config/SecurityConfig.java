package com.agroo.agroo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // ============================================================
                        // PUBLIC ENDPOINTS - GUEST access (no authentication required)
                        // ============================================================
                        .requestMatchers("/", "/api/test").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()

                        // Product endpoints - Public view
                        .requestMatchers("/api/products/**").permitAll()

                        // Post endpoints - Public view
                        .requestMatchers("/api/posts/**").permitAll()

                        // Price endpoints - Public view
                        .requestMatchers("/api/prices/**").permitAll()

                        // Comment endpoints - Public view (GET only)
                        .requestMatchers("/api/comments/**").permitAll()

                        // Like endpoints - Public view (GET only)
                        .requestMatchers("/api/likes/**").permitAll()

                        // ============================================================
                        // MACHINE RENTAL ENDPOINTS
                        // ============================================================
                        // Public - Anyone can view machine listings
                        .requestMatchers("/api/machines/**").permitAll()

                        // ============================================================
                        // REGISTERED_USER ENDPOINTS - need authentication
                        // ============================================================
                        .requestMatchers("/api/user/**").hasRole("REGISTERED_USER")

                        // Product CRUD (create, update, delete)
                        .requestMatchers("/api/products/create/**").hasRole("REGISTERED_USER")
                        .requestMatchers("/api/products/**").hasRole("REGISTERED_USER")

                        // Post CRUD (create, update, delete)
                        .requestMatchers("/api/posts/create/**").hasRole("REGISTERED_USER")
                        .requestMatchers("/api/posts/**").hasRole("REGISTERED_USER")

                        // Comment CRUD (create, update, delete)
                        .requestMatchers("/api/comments/**").hasRole("REGISTERED_USER")

                        // Like CRUD (create, delete)
                        .requestMatchers("/api/likes/**").hasRole("REGISTERED_USER")

                        // Chat Group endpoints
                        .requestMatchers("/api/groups/**").hasRole("REGISTERED_USER")
                        .requestMatchers("/api/messages/**").hasRole("REGISTERED_USER")

                        // Machine Rental CRUD (create, update, delete)
                        .requestMatchers("/api/machines").hasRole("REGISTERED_USER")
                        .requestMatchers("/api/machines/*").hasRole("REGISTERED_USER")
                        .requestMatchers("/api/machines/images/**").hasRole("REGISTERED_USER")

                        // ============================================================
                        // ADMIN ENDPOINTS - only ADMIN role
                        // ============================================================
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // ============================================================
                        // All other requests need authentication
                        // ============================================================
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}