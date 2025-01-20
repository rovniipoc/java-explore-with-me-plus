package ru.practicum.ewm.event.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateEventUserRequest {
    private String annotation;
    private String description;
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    // Статусное действие (например, SEND_TO_REVIEW, CANCEL_REVIEW)
    private String stateAction;
    private String title;
    private Long category;
}
