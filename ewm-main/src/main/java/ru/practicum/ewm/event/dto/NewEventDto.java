package ru.practicum.ewm.event.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewEventDto {
    private String annotation;
    private String description;
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid = false;
    private Integer participantLimit = 0;
    private Boolean requestModeration = true;
    private String title;
    private Long category;
}
