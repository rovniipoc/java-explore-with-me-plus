package ru.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.ewm.validation.CreateGroup;

import java.util.Set;

@Data
public class NewCompilationDto {

    @NotBlank(groups = CreateGroup.class)
    @Size(min = 1, max = 50, message = "Длина названия должна быть >= 1 символа и <= 50", groups = CreateGroup.class)
    private String title;

    @NotNull(groups = CreateGroup.class)
    private Boolean pinned;

    private Set<Long> events;
}
