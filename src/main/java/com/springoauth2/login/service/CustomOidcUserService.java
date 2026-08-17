package com.springoauth2.login.service;

import com.springoauth2.login.entity.UserEntity;
import com.springoauth2.login.enums.AuthProvider;
import com.springoauth2.login.repo.UserRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;

    public CustomOidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();

        UserEntity user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            user = new UserEntity();
            user.setEmail(email);
            user.setName(name);
            user.setEnabled(true);
            user.setProvider(AuthProvider.GOOGLE);
            user.setPassword(null);
        } else {
            user.setName(name);
            user.setProvider(AuthProvider.GOOGLE);
        }

        userRepository.save(user);

        return oidcUser;
    }
}