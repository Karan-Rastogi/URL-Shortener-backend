package com.example.URLShortner.repository;

import com.example.URLShortner.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByShortCode(String shortCode);
    Optional<Url> findByShortCodeAndIsActiveTrue(String shortCode);
    List<Url> findByUserIdAndIsActiveTrue(Long userId);
    boolean existsByCustomAlias(String customAlias);
    List<Url> findByExpireAtBeforeAndIsActiveTrue(LocalDateTime now);

    @Modifying
    @Query("UPDATE Url u SET u.clickCount = u.clickCount + 1 WHERE u.shortCode = :shortCode")
    void incrementClickCount(String shortCode);
}
