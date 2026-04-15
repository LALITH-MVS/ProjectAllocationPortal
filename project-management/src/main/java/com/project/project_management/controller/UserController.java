package com.project.project_management.controller;

import com.project.project_management.dto.LoginRequest;
import com.project.project_management.entity.User;
import com.project.project_management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public User signup(@RequestBody User user) {
        return userService.signup(user);
    }

//    @PostMapping("/login")
//    public User login(@RequestBody LoginRequest request) {
//        return userService.login(request.getEmail(), request.getPassword());
//    }
}