package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Аннотация не должна быть пустой")
    @Size(min = 20, max = 2000, message = "Длина аннотации должна быть от 20 до 2000 символов")

    private String annotation;

    // Полное описание
    @Column(nullable = false)
    @NotBlank(message = "Описание не должно быть пустым")
    @Size(min = 20, max = 7000, message = "Длина описания должна быть от 20 до 7000 символов")

    private String description;

    // Дата и время, на которые намечено событие
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Future(message = "Дата события должна быть в будущем")
    private LocalDateTime eventDate;

    // Вложенный объект Location (lat, lon)
    @Embedded
    @Valid
    private Location location;

    // Нужно ли оплачивать участие
    private boolean paid;

    // Ограничение на количество участников (0 = без ограничений)
    @Column(nullable = false, columnDefinition = "integer default 0")
    @PositiveOrZero(message = "Лимит участников должен быть положительным числом или равен нулю")

    private int participantLimit = 0;

    // Нужно ли подтверждать заявки на участие организатором
    private boolean requestModeration;

    // Текущий статус события
    @Enumerated(EnumType.STRING)
    private EventState state;

    // Заголовок
    @Column(nullable = false)
    @NotBlank(message = "Заголовок не должен быть пустым")
    @Size(min = 3, max = 120, message = "Длина заголовка должна быть от 3 до 120 символов")

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
    @Valid
    private User initiator;

    // Категория события
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @Valid
    private Category category;

    @Column(name = "views", nullable = false)
    @PositiveOrZero
    private Long views = 0L;

    @Column(name = "confirmed_requests", nullable = false)
    @PositiveOrZero
    private Long confirmedRequests = 0L;
}
