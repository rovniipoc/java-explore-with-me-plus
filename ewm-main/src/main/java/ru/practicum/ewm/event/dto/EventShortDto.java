package ru.practicum.ewm.event.dto;

import lombok.Data;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
public class EventShortDto {
    private Long id;
    private String annotation;
    private LocalDateTime eventDate;
    private boolean paid;
    private String title;
    private Long confirmedRequests;
    private Long views;
    private UserShortDto initiator;
    private CategoryDto category;
}
