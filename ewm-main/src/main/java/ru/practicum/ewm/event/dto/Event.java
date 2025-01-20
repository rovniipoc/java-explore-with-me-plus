package ru.practicum.ewm.event.dto;

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
    @Column(nullable = false, length = 2000)
    private String annotation;

    // Полное описание
    @Column(nullable = false, length = 7000)
    private String description;

    // Дата и время, на которые намечено событие
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
    @Column(nullable = false, length = 120)
    private String title;

    // Дата и время создания события
    private LocalDateTime createdOn;

    // Дата и время публикации события
    private LocalDateTime publishedOn;

    // Инициатор события
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    private User initiator;

    // Категория события
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}
