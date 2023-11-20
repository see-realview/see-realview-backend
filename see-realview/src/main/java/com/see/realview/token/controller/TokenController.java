package com.see.realview.token.controller;

import com.see.realview.token.service.TokenServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenController {

    private final TokenServiceImpl tokenService;


    @PostMapping("/refresh")
    public ResponseEntity<?> refresh() {
        return ResponseEntity.ok().body(null);
    }
}
