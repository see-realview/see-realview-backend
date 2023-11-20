package com.see.realview.core.security;

import com.see.realview.core.exception.ExceptionStatus;
import com.see.realview.core.exception.NotFoundException;
import com.see.realview.user.entity.UserAccount;
import com.see.realview.user.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;


    public CustomUserDetailsService(@Autowired UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findUserAccountByEmail(email)
                .orElseThrow(() -> new NotFoundException(ExceptionStatus.EMAIL_NOT_FOUND));

        return new CustomUserDetails(userAccount);
    }
}
