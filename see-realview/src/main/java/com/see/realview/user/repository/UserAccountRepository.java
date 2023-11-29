package com.see.realview.user.repository;

import com.see.realview.user.entity.UserAccount;

import java.util.Optional;

public interface UserAccountRepository {

    Optional<UserAccount> findUserAccountByEmail(String email);

    void save(UserAccount userAccount);
}
