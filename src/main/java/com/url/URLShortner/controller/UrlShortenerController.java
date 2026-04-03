package com.url.URLShortner.controller;

import com.url.URLShortner.dto.ShortenRequest;
import com.url.URLShortner.service.UrlShortenerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UrlShortenerController {

    private final UrlShortenerService service;
    @PostMapping("/api/shorten")
    public ResponseEntity<Map<String, String>> shorten(
            @Valid @RequestBody ShortenRequest request) {

        LocalDateTime expiryDate = null;
        if (request.getExpiryDate() != null && !request.getExpiryDate().isEmpty()) {
            expiryDate = LocalDateTime.parse(request.getExpiryDate());
        }

        String shortCode = service.shortenUrl(request.getUrl(), expiryDate);
        String shortUrl = "https://urlshortner-vx6b.onrender.com/" + shortCode;

        return ResponseEntity.ok(Map.of("shortUrl", shortUrl));
    }
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl = service.getOriginalUrl(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}