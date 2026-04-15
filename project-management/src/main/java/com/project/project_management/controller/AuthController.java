package com.project.project_management.controller;

import com.project.project_management.dto.LoginRequest;
import com.project.project_management.utility.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    // 🔐 LOGIN API
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        // Step 1: Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Step 2: Load user details (VERY IMPORTANT)
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        // Step 3: Generate JWT token using UserDetails
        return jwtUtil.generateToken(userDetails);
    }
}