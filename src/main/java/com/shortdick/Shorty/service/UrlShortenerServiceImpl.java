package com.shortdick.Shorty.service;

import com.shortdick.Shorty.dtos.request.UrlRequestDto;
import com.shortdick.Shorty.dtos.response.UrlResponseDto;
import com.shortdick.Shorty.entity.UrlEntity;
import com.shortdick.Shorty.mapper.UrlMapper;
import com.shortdick.Shorty.repository.UrlRepository;
import com.shortdick.Shorty.util.Base62Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UrlShortenerServiceImpl implements  UrlShortenerService{
    private final UrlMapper urlMapper;
    private final UrlRepository urlRepository;

    public UrlShortenerServiceImpl(UrlMapper urlMapper, UrlRepository urlRepository) {
        this.urlMapper = urlMapper;
        this.urlRepository = urlRepository;
    }

    @Override
    @Transactional
    public UrlResponseDto acortarLink(UrlRequestDto requestDto) {

        Optional<UrlEntity> urlEntityOptional = urlRepository.findByLongUrl(requestDto.getLongUrl());

        if(urlEntityOptional.isPresent()) {
            return urlMapper.toDto(urlEntityOptional.get());
        }

        UrlEntity urlEntity = urlMapper.toEntity(requestDto);
        urlEntity = urlRepository.save(urlEntity);
        String shortCode = Base62Converter.encode(urlEntity.getId());
        urlEntity.setShortCode(shortCode);
        urlRepository.save(urlEntity);
        return urlMapper.toDto(urlEntity);

    }
}
