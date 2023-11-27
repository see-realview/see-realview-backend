package com.see.realview.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.see.realview.user.entity.QUserAccount;
import com.see.realview.user.entity.UserAccount;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserAccountRepositoryImpl implements UserAccountRepository {

    private final EntityManager entityManager;

    private final JPAQueryFactory jpaQueryFactory;

    private final static QUserAccount TABLE = QUserAccount.userAccount;


    public UserAccountRepositoryImpl(@Autowired EntityManager entityManager,
                                     @Autowired JPAQueryFactory jpaQueryFactory) {
        this.entityManager = entityManager;
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

    @Override
    public void save(UserAccount userAccount) {
        entityManager.persist(userAccount);
    }
}
