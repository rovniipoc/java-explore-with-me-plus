package ru.practicum.ewm;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
public class EndpointHitInputDto {
    @NotBlank
    private String app; // Название приложения

    @NotBlank
    private String uri; // URI запроса

    @NotBlank
    private String ip; // IP пользователя

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp; // Временная метка запроса
}
