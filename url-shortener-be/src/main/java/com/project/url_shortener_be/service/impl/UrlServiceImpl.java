package com.project.url_shortener_be.service.impl;

import com.project.url_shortener_be.dto.CreateUrlRequest;
import com.project.url_shortener_be.dto.CreateUrlAutoRequest;
import com.project.url_shortener_be.dto.UrlResponse;
import com.project.url_shortener_be.entity.Url;
import com.project.url_shortener_be.repository.UrlRepository;
import com.project.url_shortener_be.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UrlServiceImpl implements UrlService {
    
    private final UrlRepository urlRepository;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    @Override
    public UrlResponse createShortUrl(CreateUrlRequest request) {
        log.info("Creating short URL for: {}", request.getOriginalUrl());
        
        // Check if custom short code already exists
        if (urlRepository.existsByShortCode(request.getCustomShortCode())) {
            throw new RuntimeException("Short code already exists: " + request.getCustomShortCode());
        }
        
        // Create new URL entity
        Url url = new Url(request.getOriginalUrl(), request.getCustomShortCode());
        url.setExpiresAt(request.getExpiresAt());
        
        // Save to database
        Url savedUrl = urlRepository.save(url);
        
        log.info("Created short URL with code: {}", savedUrl.getShortCode());
        return convertToResponse(savedUrl);
    }
    
    @Override
    public UrlResponse createShortUrlAuto(CreateUrlAutoRequest request) {
        log.info("Creating short URL with auto-generated code for: {}", request.getOriginalUrl());
        
        // Generate unique short code
        String shortCode = generateUniqueShortCode();
        
        // Create new URL entity
        Url url = new Url(request.getOriginalUrl(), shortCode);
        url.setExpiresAt(request.getExpiresAt());
        
        // Save to database
        Url savedUrl = urlRepository.save(url);
        
        log.info("Created short URL with auto-generated code: {}", savedUrl.getShortCode());
        return convertToResponse(savedUrl);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UrlResponse getUrlByShortCode(String shortCode) {
        log.info("Getting URL for short code: {}", shortCode);
        
        Url url = urlRepository.findByShortCodeAndIsActiveTrue(shortCode)
                .orElseThrow(() -> new RuntimeException("URL not found or inactive: " + shortCode));
        
        // Check if URL is expired
        if (url.getExpiresAt() != null && url.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("URL has expired: " + shortCode);
        }
        
        return convertToResponse(url);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UrlResponse> getAllActiveUrls() {
        log.info("Getting all active URLs");
        
        List<Url> urls = urlRepository.findByIsActiveTrue();
        return urls.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public UrlResponse updateUrl(Long id, CreateUrlRequest request) {
        log.info("Updating URL with ID: {}", id);
        
        Url url = urlRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("URL not found with ID: " + id));
        
        // Check if new short code already exists (if changed)
        if (!url.getShortCode().equals(request.getCustomShortCode()) && 
            urlRepository.existsByShortCode(request.getCustomShortCode())) {
            throw new RuntimeException("Short code already exists: " + request.getCustomShortCode());
        }
        
        url.setOriginalUrl(request.getOriginalUrl());
        url.setShortCode(request.getCustomShortCode());
        url.setExpiresAt(request.getExpiresAt());
        
        Url updatedUrl = urlRepository.save(url);
        log.info("Updated URL with ID: {}", id);
        
        return convertToResponse(updatedUrl);
    }
    
    @Override
    public void deleteUrl(Long id) {
        log.info("Deleting URL with ID: {}", id);
        
        if (!urlRepository.existsById(id)) {
            throw new RuntimeException("URL not found with ID: " + id);
        }
        
        urlRepository.deleteById(id);
        log.info("Deleted URL with ID: {}", id);
    }
    
    @Override
    public void incrementClickCount(String shortCode) {
        log.info("Incrementing click count for short code: {}", shortCode);
        
        urlRepository.findByShortCode(shortCode).ifPresent(url -> {
            url.setClickCount(url.getClickCount() + 1);
            urlRepository.save(url);
        });
    }
    
    @Override
    public void deactivateExpiredUrls() {
        log.info("Deactivating expired URLs");
        
        List<Url> expiredUrls = urlRepository.findExpiredUrls(LocalDateTime.now());
        expiredUrls.forEach(url -> {
            url.setIsActive(false);
            urlRepository.save(url);
        });
        
        log.info("Deactivated {} expired URLs", expiredUrls.size());
    }
    
    @Override
    public String generateUniqueShortCode() {
        String shortCode;
        do {
            shortCode = generateRandomShortCode();
        } while (urlRepository.existsByShortCode(shortCode));
        
        return shortCode;
    }
    
    // Helper method to convert entity to DTO
    private UrlResponse convertToResponse(Url url) {
        UrlResponse response = new UrlResponse();
        response.setId(url.getId());
        response.setOriginalUrl(url.getOriginalUrl());
        response.setShortCode(url.getShortCode());
        response.setShortUrl(baseUrl + "/" + url.getShortCode());
        response.setClickCount(url.getClickCount());
        response.setIsActive(url.getIsActive());
        response.setExpiresAt(url.getExpiresAt());
        response.setCreatedAt(url.getCreatedAt());
        response.setUpdatedAt(url.getUpdatedAt());
        return response;
    }
    
    // Helper method to generate random short code
    private String generateRandomShortCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return sb.toString();
    }
}
