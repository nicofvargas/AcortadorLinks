package com.shortdick.Shorty.service;

import com.shortdick.Shorty.entity.UrlEntity;
import com.shortdick.Shorty.mapper.UrlMapper;
import com.shortdick.Shorty.repository.UrlRepository;
import com.shortdick.Shorty.util.Base62Converter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UrlRedirectServiceImpl implements UrlRedirectService{
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;

    public UrlRedirectServiceImpl(UrlRepository urlRepository, UrlMapper urlMapper) {
        this.urlRepository = urlRepository;
        this.urlMapper = urlMapper;
    }


    @Override
    @Transactional
    public Optional<String> getLongUrlByShortCode(String shortCode) {
        Long id = Base62Converter.decode(shortCode);
        Optional<UrlEntity> optionalUrlEntity = urlRepository.findById(id);

        if(optionalUrlEntity.isPresent()) {
            UrlEntity urlEntity = optionalUrlEntity.get();
            int visitCount = urlEntity.getVisitCount() + 1;
            urlEntity.setVisitCount(visitCount);
            urlRepository.save(urlEntity);
            return Optional.of(urlEntity.getLongUrl());
        }
        return Optional.empty();
    }
}
