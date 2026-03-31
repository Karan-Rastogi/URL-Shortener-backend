package com.example.URLShortner.controller;

import com.example.URLShortner.dto.request.ShortenRequest;
import com.example.URLShortner.dto.response.UrlResponse;
import com.example.URLShortner.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    // Shorten a URL
    @PostMapping("/api/urls/shorten")
    public ResponseEntity<UrlResponse> shorten(
            @Valid @RequestBody ShortenRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        // userDetails is null for guests (not logged in)
        String email = userDetails != null
                ? userDetails.getUsername()
                : null;

        return ResponseEntity.ok(urlService.shorter(request, email));
    }

    // Redirect short URL → long URL
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode) {

        String longUrl = urlService.getOriginalUrl(shortCode);
        return ResponseEntity
                .status(302)
                .location(URI.create(longUrl))
                .build();
    }

    // Get all URLs for logged-in user
    @GetMapping("/api/urls/my")
    public ResponseEntity<List<UrlResponse>> getMyUrls(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                urlService.getMyUrls(userDetails.getUsername()));
    }

    // Delete a URL
    @DeleteMapping("/api/urls/{shortCode}")
    public ResponseEntity<String> deleteUrl(
            @PathVariable String shortCode,
            @AuthenticationPrincipal UserDetails userDetails) {

        urlService.deleteUrl(shortCode, userDetails.getUsername());
        return ResponseEntity.ok("URL deleted successfully");
    }
}