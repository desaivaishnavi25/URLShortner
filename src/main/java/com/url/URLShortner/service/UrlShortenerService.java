package com.url.URLShortner.service;

import com.url.URLShortner.Entity.UrlMapping;
import com.url.URLShortner.Repository.UrlMappingRepository;
import com.url.URLShortner.exception.UrlException;
import com.url.URLShortner.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlShortenerService {

    private final UrlMappingRepository repository;
    private final Base62Encoder base62Encoder;

    public String shortenUrl(String originalUrl, LocalDateTime expiryDate) {
        Optional<UrlMapping> existing = repository.findByOriginalUrl(originalUrl);
        if (existing.isPresent()) {
            return existing.get().getShortCode();
        }
        UrlMapping mapping = UrlMapping.builder()
                .originalUrl(originalUrl)
                .shortCode("temp")
                .createdAt(LocalDateTime.now())
                .expiryDate(expiryDate)
                .clickCount(0L)
                .build();
        UrlMapping saved = repository.save(mapping);
        String shortCode = base62Encoder.encode(saved.getId());
        saved.setShortCode(shortCode);
        repository.save(saved);

        return shortCode;
    }

    public String getOriginalUrl(String shortCode) {

        UrlMapping mapping = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlException("Short URL not found!"));
        if (mapping.getExpiryDate() != null &&
                mapping.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UrlException("This URL has expired!");
        }
        mapping.setClickCount(mapping.getClickCount() + 1);
        repository.save(mapping);

        return mapping.getOriginalUrl();
    }
}