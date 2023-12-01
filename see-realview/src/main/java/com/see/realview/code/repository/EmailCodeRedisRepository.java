package com.see.realview.code.repository;

import com.see.realview.code.entity.EmailCode;

import java.util.Optional;

public interface EmailCodeRedisRepository {

    void save(String email, String code);

    Optional<EmailCode> findCodeByEmail(String email);

    void authenticated(String email);

    void delete(String email);
}
