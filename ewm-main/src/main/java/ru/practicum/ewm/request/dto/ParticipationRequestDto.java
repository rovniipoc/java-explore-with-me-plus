package ru.practicum.ewm.request.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ParticipationRequestDto {
    private Long id;
    private LocalDateTime created;
    private Long event;
    private Long requester;
    private String status; // "PENDING"/"CONFIRMED"/"REJECTED"/"CANCELED"
}
