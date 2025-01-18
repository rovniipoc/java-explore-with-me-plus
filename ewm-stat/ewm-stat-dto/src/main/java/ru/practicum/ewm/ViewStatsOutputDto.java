package ru.practicum.ewm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ViewStatsOutputDto {
    private String app; // Название приложения
    private String uri; // URI запроса
    private long hits; // Количество посещений
}
