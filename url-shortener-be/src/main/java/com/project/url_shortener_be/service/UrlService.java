package com.project.url_shortener_be.service;

import com.project.url_shortener_be.dto.CreateUrlRequest;
import com.project.url_shortener_be.dto.CreateUrlAutoRequest;
import com.project.url_shortener_be.dto.UrlResponse;

import java.util.List;

public interface UrlService {
    
    // Create new short URL with custom code
    UrlResponse createShortUrl(CreateUrlRequest request);
    
    // Create new short URL with auto-generated code
    UrlResponse createShortUrlAuto(CreateUrlAutoRequest request);
    
    // Get URL by short code
    UrlResponse getUrlByShortCode(String shortCode);
    
    // Get all active URLs
    List<UrlResponse> getAllActiveUrls();
    
    // Update URL
    UrlResponse updateUrl(Long id, CreateUrlRequest request);
    
    // Delete URL
    void deleteUrl(Long id);
    
    // Increment click count
    void incrementClickCount(String shortCode);
    
    // Deactivate expired URLs
    void deactivateExpiredUrls();
    
    // Generate unique short code
    String generateUniqueShortCode();
}
