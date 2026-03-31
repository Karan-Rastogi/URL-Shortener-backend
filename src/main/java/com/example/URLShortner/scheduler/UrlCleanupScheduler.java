package com.example.URLShortner.scheduler;

import com.example.URLShortner.model.Url;
import com.example.URLShortner.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UrlCleanupScheduler {

    private final UrlRepository urlRepository;

    // Runs every day at midnight
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupExpiredUrls() {

        log.info("Running URL cleanup job at {}", LocalDateTime.now());

        List<Url> expiredUrls = urlRepository
                .findByExpireAtBeforeAndIsActiveTrue(LocalDateTime.now());

        if (expiredUrls.isEmpty()) {
            log.info("No expired URLs found");
            return;
        }

        expiredUrls.forEach(url -> {
            url.setIsActive(false);
            log.info("Deactivating expired URL: {}", url.getShortCode());
        });

        urlRepository.saveAll(expiredUrls);

        log.info("Cleanup complete. Deactivated {} URLs", expiredUrls.size());
    }
}