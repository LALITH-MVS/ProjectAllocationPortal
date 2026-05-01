package com.project.project_management.security;

import com.project.project_management.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.GetMapping;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    // 🔐 Password encoder (IMPORTANT)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 🔐 Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 🔐 Main Security Config
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> {})   // ✅ FIXED
                .csrf(csrf -> csrf.disable())   // ✅ FIXED

                .authorizeHttpRequests(auth -> auth

                        // 🔓 PUBLIC APIs
                        .requestMatchers("/auth/**", "/api/users/signup").permitAll()

                        // 👨‍🏫 TEACHER ONLY
                        .requestMatchers("/api/classes/**").hasRole("TEACHER")
                        .requestMatchers("/api/ideas/approve/**").hasRole("TEACHER")
                        .requestMatchers("/audit/**").hasRole("TEACHER")
                        .requestMatchers("/project/add").hasRole("TEACHER")
                        .requestMatchers("/project/delete/**").hasRole("TEACHER")
                        .requestMatchers("/api/ideas/approve/**").hasRole("TEACHER")
                        .requestMatchers("/ideas/update-status").hasRole("TEACHER")

                        .requestMatchers("/ideas/class/**").hasRole("TEACHER")

                        // 🎓 STUDENT ONLY
                        .requestMatchers("/api/ideas/submit/**").hasRole("STUDENT")
                        .requestMatchers("/student/**").hasRole("STUDENT")
                        .requestMatchers("/ideas/submit").hasRole("STUDENT")
                        .requestMatchers("/available/**").hasRole("STUDENT")
                        .requestMatchers("/selected/**").hasRole("STUDENT")
                        .requestMatchers("/project/select").hasRole("STUDENT")
                        // 👥 COMMON (logged-in users)
                        .requestMatchers("/api/team/**").authenticated()
                        .requestMatchers("/project/select/**").hasRole("STUDENT")
                        .requestMatchers("/api/projects/**").authenticated()
                        .requestMatchers("/student/**").hasRole("STUDENT")
                        // 🔒 EVERYTHING ELSE
                        .anyRequest().authenticated()
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // 🔥 JWT FILTER
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}


