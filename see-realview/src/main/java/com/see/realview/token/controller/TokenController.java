package com.see.realview.token.controller;

import com.see.realview._core.response.Response;
import com.see.realview._core.response.ResponseData;
import com.see.realview._core.security.CustomUserDetails;
import com.see.realview.token.entity.Token;
import com.see.realview.token.entity.constants.Header;
import com.see.realview.token.service.TokenServiceImpl;
import com.see.realview.user.entity.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
public class TokenController {

    private final TokenServiceImpl tokenService;


    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserAccount userAccount = userDetails.userAccount();
        Token token = tokenService.refresh(userAccount);
        return ResponseEntity
                .ok()
                .header(Header.AUTHORIZATION.value(), token.accessToken())
                .header(Header.REFRESH.value(), token.refreshToken())
                .body(Response.of());
    }
}
