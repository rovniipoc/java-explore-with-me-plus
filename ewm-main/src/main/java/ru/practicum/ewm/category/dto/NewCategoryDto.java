package ru.practicum.ewm.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewCategoryDto {

    @NotBlank
    @Size(min = 1, max = 50, message = "Длина названия должна быть больше 1 символа и меньше 50")
    private String name;
}
