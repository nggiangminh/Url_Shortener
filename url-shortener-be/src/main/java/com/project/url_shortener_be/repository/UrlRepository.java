package com.project.url_shortener_be.repository;

import com.project.url_shortener_be.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    
    // Find by short code
    Optional<Url> findByShortCode(String shortCode);
    
    // Find by short code and check if active
    Optional<Url> findByShortCodeAndIsActiveTrue(String shortCode);
    
    // Find all active URLs
    List<Url> findByIsActiveTrue();
    
    // Find expired URLs
    @Query("SELECT u FROM Url u WHERE u.expiresAt IS NOT NULL AND u.expiresAt < :currentTime")
    List<Url> findExpiredUrls(@Param("currentTime") LocalDateTime currentTime);
    
    // Check if short code exists
    boolean existsByShortCode(String shortCode);
    
    // Find URLs by original URL (for duplicate checking)
    List<Url> findByOriginalUrl(String originalUrl);
}
