package com.shortdick.Shorty.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "urls")
public class UrlEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 2048)
    private String longUrl;

    @Column(nullable = true, unique = true, length = 10)
    private String shortCode;

    @Column
    private int visitCount = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(nullable = true)
    private LocalDateTime expirationDate;


    @PrePersist
    public void onPrePersist() {
        this.creationDate = LocalDateTime.now();
    }

    //constructor
    public UrlEntity(String longUrl, String shortCode) {
        this.longUrl = longUrl;
        this.shortCode = shortCode;
    }
}
