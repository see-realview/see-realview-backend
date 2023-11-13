package com.see.realview.user.controller;

import com.see.realview.core.response.Response;
import com.see.realview.user.dto.request.RegisterRequest;
import com.see.realview.user.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok().body(Response.success(null));
    }
}
