package com.springoauth2.login.service;

import com.springoauth2.login.entity.UserEntity;
import com.springoauth2.login.repo.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with the given email address"));
        if (!user.isEnabled()) throw new UsernameNotFoundException("Account not verified");
        return new User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList() // no roles/authorities for now — keep it simple
        );
    }
}
