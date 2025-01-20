package ru.practicum.ewm.event.dto;

import lombok.Data;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
public class EventFullDto {
    private Long id;
    private String annotation;
    private String description;
    private LocalDateTime eventDate;
    private Location location;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    private EventState state;
    private String title;
    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
    private UserShortDto initiator;
    private CategoryDto category;
    private Long confirmedRequests; // Количество подтверждённых заявок
    private Long views; // Количество просмотров
}