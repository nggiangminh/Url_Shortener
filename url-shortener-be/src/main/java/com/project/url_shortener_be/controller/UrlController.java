package com.project.url_shortener_be.controller;

import com.project.url_shortener_be.dto.CreateUrlRequest;
import com.project.url_shortener_be.dto.CreateUrlAutoRequest;
import com.project.url_shortener_be.dto.UrlResponse;
import com.project.url_shortener_be.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UrlController {
    
    private final UrlService urlService;
    
    // Create new short URL with custom code
    @PostMapping("/custom")
    public ResponseEntity<UrlResponse> createShortUrl(@Valid @RequestBody CreateUrlRequest request) {
        log.info("Received request to create short URL with custom code for: {}", request.getOriginalUrl());
        
        try {
            UrlResponse response = urlService.createShortUrl(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating short URL: {}", e.getMessage());
            throw e;
        }
    }
    
    // Create new short URL with auto-generated code
    @PostMapping
    public ResponseEntity<UrlResponse> createShortUrlAuto(@Valid @RequestBody CreateUrlAutoRequest request) {
        log.info("Received request to create short URL with auto-generated code for: {}", request.getOriginalUrl());
        
        try {
            UrlResponse response = urlService.createShortUrlAuto(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating short URL: {}", e.getMessage());
            throw e;
        }
    }
    
    // Get URL by short code (for redirect)
    @GetMapping("/{shortCode}")
    public ResponseEntity<UrlResponse> getUrlByShortCode(@PathVariable String shortCode) {
        log.info("Received request to get URL for short code: {}", shortCode);
        
        try {
            UrlResponse response = urlService.getUrlByShortCode(shortCode);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting URL for short code {}: {}", shortCode, e.getMessage());
            throw e;
        }
    }
    
    // Redirect to original URL
    @GetMapping("/redirect/{shortCode}")
    public void redirectToOriginalUrl(@PathVariable String shortCode, HttpServletResponse response) {
        log.info("Redirecting short code: {}", shortCode);
        
        try {
            UrlResponse urlResponse = urlService.getUrlByShortCode(shortCode);
            
            // Increment click count
            urlService.incrementClickCount(shortCode);
            
            // Redirect to original URL
            response.sendRedirect(urlResponse.getOriginalUrl());
            
        } catch (IOException e) {
            log.error("Error redirecting: {}", e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        } catch (Exception e) {
            log.error("Error getting URL for redirect: {}", e.getMessage());
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
    }
    
    // Get all active URLs
    @GetMapping
    public ResponseEntity<List<UrlResponse>> getAllActiveUrls() {
        log.info("Received request to get all active URLs");
        
        try {
            List<UrlResponse> urls = urlService.getAllActiveUrls();
            return ResponseEntity.ok(urls);
        } catch (Exception e) {
            log.error("Error getting all active URLs: {}", e.getMessage());
            throw e;
        }
    }
    
    // Update URL
    @PutMapping("/{id}")
    public ResponseEntity<UrlResponse> updateUrl(@PathVariable Long id, 
                                               @Valid @RequestBody CreateUrlRequest request) {
        log.info("Received request to update URL with ID: {}", id);
        
        try {
            UrlResponse response = urlService.updateUrl(id, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating URL with ID {}: {}", id, e.getMessage());
            throw e;
        }
    }
    
    // Delete URL
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUrl(@PathVariable Long id) {
        log.info("Received request to delete URL with ID: {}", id);
        
        try {
            urlService.deleteUrl(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting URL with ID {}: {}", id, e.getMessage());
            throw e;
        }
    }
    
    // Generate unique short code
    @GetMapping("/generate-code")
    public ResponseEntity<String> generateUniqueShortCode() {
        log.info("Received request to generate unique short code");
        
        try {
            String shortCode = urlService.generateUniqueShortCode();
            return ResponseEntity.ok(shortCode);
        } catch (Exception e) {
            log.error("Error generating unique short code: {}", e.getMessage());
            throw e;
        }
    }
}
