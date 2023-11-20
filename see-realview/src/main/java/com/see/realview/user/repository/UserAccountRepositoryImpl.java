package com.see.realview.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.see.realview.user.entity.QUserAccount;
import com.see.realview.user.entity.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserAccountRepositoryImpl implements UserAccountRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final static QUserAccount TABLE = QUserAccount.userAccount;


    public UserAccountRepositoryImpl(@Autowired JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Optional<UserAccount> findUserAccountByEmail(String email) {
        UserAccount userAccount = jpaQueryFactory
                .selectFrom(TABLE)
                .where(TABLE.email.eq(email))
                .fetchOne();

        return Optional.ofNullable(userAccount);
    }
}
