package com.agroo.agroo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                        // STATIC RESOURCES & UPLOADS
                        // ============================================================
                        .requestMatchers("/uploads/**", "/images/**", "/static/**").permitAll()

                        // ============================================================
                        // WEATHER SYSTEM - PUBLIC ACCESS
                        // ============================================================
                        // ✅ Weather data - Anyone can view (No login required)
                        .requestMatchers("/api/weather/**").permitAll()

                        // ⚠️ Weather Alerts - Only logged-in users receive emails
                        // The email sending is handled in EmailNotificationServiceImpl
                        // which checks for authenticated user

                        // ============================================================
                        // GENERAL PUBLIC ENDPOINTS
                        // ============================================================
                        .requestMatchers("/", "/api/test", "/api/auth/**", "/api/public/**", "/api/chat/**").permitAll()

                        // ============================================================
                        // WEBSOCKET ENDPOINTS
                        // ============================================================
                        .requestMatchers("/ws/**", "/ws", "/ws/info").permitAll()

                        // ============================================================
                        // PUBLIC VIEWS (GET requests only)
                        // ============================================================
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/prices/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/comments/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/likes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/machines/**").permitAll()

                        // ============================================================
                        // ADMIN ENDPOINTS
                        // ============================================================
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // ============================================================
                        // AUTHENTICATED ACTIONS
                        // ============================================================
                        .requestMatchers("/api/user/**").hasAnyRole("REGISTERED_USER", "ADMIN")
                        .requestMatchers("/api/groups/**", "/api/messages/**").hasAnyRole("REGISTERED_USER", "ADMIN")

                        // All other requests require authentication
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

        // Allow all origins that your frontend might use
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "http://192.168.*.*:*",
                "http://*.agroo.lk"
        ));

        // Allow all methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));

        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "Cache-Control",
                "Pragma",
                "Expires"
        ));

        // Expose headers for frontend access
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Disposition",
                "Content-Type"
        ));

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