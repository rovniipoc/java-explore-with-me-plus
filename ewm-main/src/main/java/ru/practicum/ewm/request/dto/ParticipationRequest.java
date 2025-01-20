package ru.practicum.ewm.request.dto;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.ewm.event.dto.Event;
import ru.practicum.ewm.user.dto.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Дата и время создания заявки
    private LocalDateTime created;

    // Событие
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    // Пользователь, отправивший заявку
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private User requester;

    // Текущий статус заявки
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}