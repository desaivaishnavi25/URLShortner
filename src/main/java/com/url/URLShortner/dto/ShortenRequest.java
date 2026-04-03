package com.url.URLShortner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortenRequest {
    @NotBlank(message = "URL cannot be empty")
    @Pattern(regexp = "^(https?://).*",message = "URL must start with http:// or https://")
    private String url;
    private String expiryDate;
}