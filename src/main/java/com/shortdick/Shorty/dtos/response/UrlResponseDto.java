package com.shortdick.Shorty.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlResponseDto {
    private String shortUrl;
    private String longUrl;
    private String shortCode;
    private LocalDateTime creationDate;
    private LocalDateTime expirationDate;
}
