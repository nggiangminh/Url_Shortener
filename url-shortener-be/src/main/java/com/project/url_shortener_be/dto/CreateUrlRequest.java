package com.project.url_shortener_be.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateUrlRequest {
    
    @NotBlank(message = "Original URL is required")
    @Pattern(regexp = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?$", 
             message = "Please provide a valid URL")
    private String originalUrl;
    
    private String customShortCode;
    
    private LocalDateTime expiresAt;
}
