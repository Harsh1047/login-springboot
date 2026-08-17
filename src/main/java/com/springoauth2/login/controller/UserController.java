package com.springoauth2.login.controller;

import com.springoauth2.login.dto.LoginDTO;
import com.springoauth2.login.dto.UserDTO;
import com.springoauth2.login.entity.UserEntity;
import com.springoauth2.login.repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import com.springoauth2.login.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1.0")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public UserController(UserService userService, AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserDTO request) {
        userService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam String token) {
        userService.verifyUser(token);
        return ResponseEntity.ok("Account verified successfully. You can now log in.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginDTO login, HttpServletRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.getEmail(),login.getPassword())
        );
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", context);
        return ResponseEntity.ok("Login Succesfull");
    }

    @GetMapping("/me")
    public ResponseEntity<String> currentUser(Authentication authentication) {
        String email;
        if (authentication instanceof OAuth2AuthenticationToken token) {
            Map<String, Object> attributes = token.getPrincipal().getAttributes();
            String registrationId = token.getAuthorizedClientRegistrationId();

            email = (String) attributes.get("email");

            if (email == null && "github".equalsIgnoreCase(registrationId)) {
                email = attributes.get("login") + "@github.com";
            }
        }
        else email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return ResponseEntity.ok("Logged in as: " + user.getName() + " (" + user.getEmail() + ")");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutCurrentUser(){
        return ResponseEntity.ok("Logout Successful");
    }

}
