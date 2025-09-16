package com.shortdick.Shorty.controller;

import com.shortdick.Shorty.dtos.request.UrlRequestDto;
import com.shortdick.Shorty.dtos.response.UrlResponseDto;
import com.shortdick.Shorty.service.UrlRedirectService;
import com.shortdick.Shorty.service.UrlShortenerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
public class UrlController {
    private final UrlRedirectService urlRedirectService;
    private final UrlShortenerService urlShortenerService;


    public UrlController(UrlRedirectService urlRedirectService, UrlShortenerService urlShortenerService) {
        this.urlRedirectService = urlRedirectService;
        this.urlShortenerService = urlShortenerService;
    }

    //volver a practicar este tipo de respuesta
    @PostMapping("/api/v1/short")
    public ResponseEntity<UrlResponseDto> acortarLink(@Valid @RequestBody UrlRequestDto urlRequestDto) {
        UrlResponseDto urlResponseDto = urlShortenerService.acortarLink(urlRequestDto);

        String shortCode = urlResponseDto.getShortCode();

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath() //empiezo desde el dominio/
                .path("{shortCode}") //plantilla de ruta
                .buildAndExpand(shortCode) //reemplaza shortCode por el valor obteniendo la url completa
                .toUri();

        return ResponseEntity.created(location).body(urlResponseDto);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToLongUrl(@PathVariable String shortCode) {
        Optional<String> optionalLongUrl = urlRedirectService.getLongUrlByShortCode(shortCode);

        if(optionalLongUrl.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.FOUND) // codigo 302 = redireccion
                    .location(URI.create(optionalLongUrl.get()))
                    .build();
        }

        return ResponseEntity.notFound().build();
    }


}
