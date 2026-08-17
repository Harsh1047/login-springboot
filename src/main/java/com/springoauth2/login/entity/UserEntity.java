package com.springoauth2.login.entity;

import com.springoauth2.login.enums.AuthProvider;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    @Nullable
    private String password;
    private boolean enabled = false;
    private String verificationToken;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Enumerated(EnumType.STRING)
    private AuthProvider provider = AuthProvider.LOCAL;
}
