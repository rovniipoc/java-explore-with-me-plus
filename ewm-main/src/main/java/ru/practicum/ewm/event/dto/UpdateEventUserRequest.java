package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateEventUserRequest {

    @NotBlank(message = "Аннотация не должна быть пустой")
    @Size(min = 20, max = 2000, message = "Длина аннотации должна быть от 20 до 2000 символов")
    private String annotation;

    @NotBlank(message = "Описание не должно быть пустым")
    @Size(min = 20, max = 7000, message = "Длина описания должна быть от 20 до 7000 символов")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Future(message = "Дата события должна быть в будущем")
    private LocalDateTime eventDate;
    @Valid

    private Location location;
    private Boolean paid;

    @PositiveOrZero(message = "Лимит участников должен быть положительным числом или равен нулю")

    private Integer participantLimit;
    private Boolean requestModeration;

    private String stateAction;
    @NotBlank(message = "Заголовок не должен быть пустым")

    @Size(min = 3, max = 120, message = "Длина заголовка должна быть от 3 до 120 символов")
    private String title;
    @NotNull
    private Long category;
}
