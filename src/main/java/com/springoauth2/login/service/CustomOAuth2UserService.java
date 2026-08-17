package com.springoauth2.login.service;

import com.springoauth2.login.entity.UserEntity;
import com.springoauth2.login.enums.AuthProvider;
import com.springoauth2.login.repo.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "google" or "github"
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email;
        String name;

        if (registrationId.equalsIgnoreCase("google")) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        } else if (registrationId.equalsIgnoreCase("github")) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            if (email == null) {
                email = attributes.get("login") + "@github.com";
            }
            if (name == null) {
                name = (String) attributes.get("login");
            }
        } else {
            throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
        }

        AuthProvider provider = registrationId.equalsIgnoreCase("google") ? AuthProvider.GOOGLE : AuthProvider.GITHUB;

        UserEntity user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            user = new UserEntity();
            user.setEmail(email);
            user.setName(name);
            user.setEnabled(true);
            user.setProvider(provider);
            user.setPassword(null);
        } else {
            user.setName(name);
            user.setProvider(provider);
        }

        userRepository.save(user);

        return oAuth2User;
    }
}