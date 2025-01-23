package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.user.dto.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Краткое описание
    @Column(nullable = false)
    private String annotation;

    // Полное описание
    @Column(nullable = false)
    private String description;

    // Дата и время, на которые намечено событие
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    // Вложенный объект Location (lat, lon)
    @Embedded
    private Location location;

    // Нужно ли оплачивать участие
    private boolean paid;

    // Ограничение на количество участников (0 = без ограничений)
    private int participantLimit;

    // Нужно ли подтверждать заявки на участие организатором
    private boolean requestModeration;

    // Текущий статус события
    @Enumerated(EnumType.STRING)
    private EventState state;

    // Заголовок
    @Column(nullable = false)
    private String title;

    // Дата и время создания события
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    // Дата и время публикации события
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    // Инициатор события
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    private User initiator;

    // Категория события
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "views", nullable = false)
    private Long views = 0L;

    @Column(name = "confirmed_requests", nullable = false)
    private Long confirmedRequests = 0L;
}
