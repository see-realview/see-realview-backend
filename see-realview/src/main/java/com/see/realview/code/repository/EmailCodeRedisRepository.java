package com.see.realview.code.repository;

import java.util.Optional;

public interface EmailCodeRedisRepository {

    void save(String email, String code);

    Optional<String> findCodeByEmail(String email);
}
