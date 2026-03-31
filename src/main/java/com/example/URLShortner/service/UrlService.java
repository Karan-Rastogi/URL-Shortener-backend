package com.example.URLShortner.service;

import com.example.URLShortner.dto.request.ShortenRequest;
import com.example.URLShortner.dto.response.UrlResponse;
import com.example.URLShortner.repository.UrlRepository;

import java.util.List;

public interface UrlService {

    UrlResponse shorter(ShortenRequest request, String userEmail);

    String getOriginalUrl(String shortCode);

    List<UrlResponse> getMyUrls(String userEmail);

    void deleteUrl(String userEmail, String shortCode);
}
