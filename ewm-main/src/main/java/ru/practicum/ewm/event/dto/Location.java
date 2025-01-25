package ru.practicum.ewm.event.dto;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Embeddable
@Data
public class Location {
    @NotNull(message = "Широта должна быть указана")
    private Double lat;
    @NotNull(message = "Широта должна быть указана")
    private Double lon;
}
