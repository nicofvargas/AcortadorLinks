package com.shortdick.Shorty.service;

import com.shortdick.Shorty.dtos.request.UrlRequestDto;
import com.shortdick.Shorty.dtos.response.UrlResponseDto;

public interface UrlShortenerService {
    public UrlResponseDto acortarLink(UrlRequestDto requestDto);
}
