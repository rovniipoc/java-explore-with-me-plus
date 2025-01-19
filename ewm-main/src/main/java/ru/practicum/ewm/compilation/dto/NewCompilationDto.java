package ru.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class NewCompilationDto {

    @NotBlank
    @Size(min = 1, max = 50, message = "Длина названия должна быть больше 1 символа и меньше 50")
    private String title;

    @NotNull
    private Boolean pinned;

    private Set<Long> events;
}
