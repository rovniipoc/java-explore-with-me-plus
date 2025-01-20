package ru.practicum.ewm.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.ewm.validation.CreateGroup;

@Data
public class NewUserRequest {

    @NotBlank(groups = CreateGroup.class)
    private String name;

    @NotBlank(groups = CreateGroup.class)
    @Email(groups = CreateGroup.class)
    private String email;
}
