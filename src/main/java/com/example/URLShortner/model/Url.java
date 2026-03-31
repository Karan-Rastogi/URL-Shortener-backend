package com.example.URLShortner.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import java.time.Instant;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "urls")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 20)
    private String shortCode;

    @Column(nullable = false, length = 2048)
    private String longUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(nullable = true)
    private LocalDateTime expireAt;

    @JoinColumn(name = "user_id", nullable = true)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(nullable = false)
    @Builder.Default
    private Long clickCount = 0L;

    @Column(nullable = true, unique = true, length = 50)
    private String customAlias;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }


}
