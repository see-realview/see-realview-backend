package com.see.realview.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.see.realview.user.entity.QUserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserAccountRepositoryImpl implements UserAccountRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final static QUserAccount TABLE = QUserAccount.userAccount;


}
