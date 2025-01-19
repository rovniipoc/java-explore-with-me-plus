package ru.practicum.ewm.compilation.dto;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
public class CompilationDto {

    private Long id;

    private String title;

    private Boolean pinned;

    private Set<EventShortDto> events;

}
