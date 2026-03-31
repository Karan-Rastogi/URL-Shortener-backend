package com.example.URLShortner.service.impl;

import com.example.URLShortner.dto.request.ShortenRequest;
import com.example.URLShortner.dto.response.UrlResponse;
import com.example.URLShortner.model.Url;
import com.example.URLShortner.model.User;
import com.example.URLShortner.repository.UrlRepository;
import com.example.URLShortner.repository.UserRepository;
import com.example.URLShortner.service.UrlService;
import com.example.URLShortner.util.Base62Encoder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final UserRepository userRepository;
    private final Base62Encoder base62Encoder;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public UrlResponse shorter(ShortenRequest request, String userEmail) {

        // 1. Handle custom alias if provided

        if (request.getCustomAlias() != null && !request.getCustomAlias().trim().isEmpty()){
            if (urlRepository.existsByCustomAlias(request.getCustomAlias())){
                throw new RuntimeException("Custom alias already taken");
            }
        }

        // 2. Find user if logged in (userEmail is null for guests)
        User user = null;

        if (userEmail != null){
            user = userRepository.findByEmail(userEmail).orElse(null);
        }

        // 3. Calculate expiry date
        LocalDateTime expiresAt = null;
        if(request.getExpiryDate() != null){
            expiresAt = LocalDateTime.now().plusDays(request.getExpiryDate());
        }

        // 4. Save URL first with null shortCode to get auto-increment id
        Url url = Url.builder()
                .longUrl(request.getLongUrl())
                .user(user)
                .expireAt(expiresAt)
                .customAlias(request.getCustomAlias())
                .isActive(true)
                .clickCount(0L)
                .build();

        Url saved = urlRepository.save(url);

        // 5. Generate shortCode from id using Base62
        String shortCode = (request.getCustomAlias() != null &&
                !request.getCustomAlias().isBlank())
                ? request.getCustomAlias()
                : base62Encoder.encode(saved.getId());

        // 6. Update the row with the shortCode
        saved.setShortCode(shortCode);
        urlRepository.save(saved);

        // 7. Build and return response
        return buildUrlResponse(saved);
    }



    @Override
    @Transactional
    public String getOriginalUrl(String shortCode) {

        Url url = urlRepository
                .findByShortCodeAndIsActiveTrue(shortCode)
                .orElseThrow(() ->
                        new RuntimeException("Short URL not found"));

        // Check expiry
        if (url.getExpireAt() != null &&
                url.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Short URL has expired");
        }

        // Increment click count
        urlRepository.incrementClickCount(shortCode);

        return url.getLongUrl();
    }

    @Override
    public List<UrlResponse> getMyUrls(String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return urlRepository
                .findByUserIdAndIsActiveTrue(user.getId())
                .stream()
                .map(this::buildUrlResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUrl(String shortCode, String userEmail) {

        Url url = urlRepository
                .findByShortCodeAndIsActiveTrue(shortCode)
                .orElseThrow(() ->
                        new RuntimeException("URL not found"));

        // Make sure this URL belongs to the requesting user
        if (url.getUser() == null ||
                !url.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Not authorized to delete this URL");
        }

        // Soft delete — set isActive to false
        url.setIsActive(false);
        urlRepository.save(url);
    }

    // Helper — converts Url entity to UrlResponse DTO
    private UrlResponse buildUrlResponse(Url url) {
        return UrlResponse.builder()
                .shortCode(url.getShortCode())
                .shortUrl(baseUrl + "/" + url.getShortCode())
                .longUrl(url.getLongUrl())
                .clickCount(url.getClickCount())
                .createdAt(url.getCreatedAt())
                .expiresAt(url.getExpireAt())
                .isActive(url.getIsActive())
                .customAlias(url.getCustomAlias())
                .build();
    }
}
