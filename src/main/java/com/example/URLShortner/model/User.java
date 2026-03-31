package com.example.URLShortner.model;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created_At;

    @Column(nullable = false)
    @Builder.Default
    private Boolean is_Enabled = true;

    @PrePersist
    public void prePersist(){
        this.created_At = LocalDateTime.now();
    }

    @Column(nullable = false)
    public Boolean getIsEnabled() {
        return true;
    }

    public enum Role{
        USER , ADMIN
    }
}
