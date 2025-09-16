package com.shortdick.Shorty.mapper;

import com.shortdick.Shorty.dtos.request.UrlRequestDto;
import com.shortdick.Shorty.dtos.response.UrlResponseDto;
import com.shortdick.Shorty.entity.UrlEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UrlMapper {

    private final String dominio;

    public UrlMapper(@Value("${app.dominio}") String dominio) {
        this.dominio = dominio;
    }

    public UrlEntity toEntity(UrlRequestDto dto) {
        UrlEntity entity = new UrlEntity();
        entity.setLongUrl(dto.getLongUrl());
        return entity;
    }

    public UrlResponseDto toDto(UrlEntity entity) {
        UrlResponseDto urlResponseDto = new UrlResponseDto();

        urlResponseDto.setShortUrl(dominio + entity.getShortCode());
        urlResponseDto.setLongUrl(entity.getLongUrl());
        urlResponseDto.setShortCode(entity.getShortCode());
        urlResponseDto.setCreationDate(entity.getCreationDate());
        urlResponseDto.setExpirationDate(entity.getExpirationDate());

        return urlResponseDto;
    }
}
