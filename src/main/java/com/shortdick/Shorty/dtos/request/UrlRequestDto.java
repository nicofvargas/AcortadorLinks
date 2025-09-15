package com.shortdick.Shorty.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlRequestDto {

    @NotBlank(message = "La URL es obligatoria.")
    @URL(message = "La URL no es válida.")
    private String longUrl;
}
