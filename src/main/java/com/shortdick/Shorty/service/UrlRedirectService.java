package com.shortdick.Shorty.service;

import com.shortdick.Shorty.entity.UrlEntity;

import java.util.Optional;

public interface UrlRedirectService {

    Optional<String> getLongUrlByShortCode(String shortCode);
}
