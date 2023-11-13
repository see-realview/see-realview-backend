package com.see.realview.user.service;

import com.see.realview.user.repository.UserAccountRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserAccountRepositoryImpl userAccountRepository;

}
