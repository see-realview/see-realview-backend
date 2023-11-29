package com.see.realview.code.service;

import com.see.realview.code.dto.VerifyEmailRequest;

public interface EmailAuthenticationService {

    void send(String email);

    void check(VerifyEmailRequest request);
}
