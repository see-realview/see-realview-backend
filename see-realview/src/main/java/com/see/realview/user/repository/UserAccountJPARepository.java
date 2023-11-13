package com.see.realview.user.repository;

import com.see.realview.user.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountJPARepository extends JpaRepository<UserAccount, Long> {
}
