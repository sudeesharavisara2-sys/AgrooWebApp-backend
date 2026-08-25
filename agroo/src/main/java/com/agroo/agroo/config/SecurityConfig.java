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
                        // PUBLIC ENDPOINTS - Accessible by GUEST users
                        // ============================================================
                        .requestMatchers("/", "/api/test").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/products/**").permitAll()      // Guest can view products
                        .requestMatchers("/api/posts/**").permitAll()         // Guest can view posts
                        .requestMatchers("/api/prices/**").permitAll()        // Guest can view prices
                        .requestMatchers("/api/fertilizer/**").permitAll()    // Guest can view fertilizers
                        .requestMatchers("/api/machinery/**").permitAll()     // Guest can view machinery

                        // ============================================================
                        // REGISTERED_USER ENDPOINTS - Only authenticated users
                        // ============================================================
                        .requestMatchers("/api/user/**").hasRole("REGISTERED_USER")
                        .requestMatchers("/api/products/create").hasRole("REGISTERED_USER")
                        .requestMatchers("/api/posts/create").hasRole("REGISTERED_USER")
                        .requestMatchers("/api/comments/**").hasRole("REGISTERED_USER")
                        .requestMatchers("/api/groups/**").hasRole("REGISTERED_USER")
                        .requestMatchers("/api/chats/**").hasRole("REGISTERED_USER")
                        .requestMatchers("/api/rentals/create").hasRole("REGISTERED_USER")
                        .requestMatchers("/api/fertilizer/create").hasRole("REGISTERED_USER")

                        // ============================================================
                        // ADMIN ENDPOINTS - Only ADMIN users
                        // ============================================================
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/posts/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/products/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/prices/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/alerts/**").hasRole("ADMIN")

                        // ============================================================
                        // DEFAULT - All other requests require authentication
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