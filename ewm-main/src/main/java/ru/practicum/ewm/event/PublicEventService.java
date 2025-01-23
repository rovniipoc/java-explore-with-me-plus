package ru.practicum.ewm.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.event.dto.EventFullDto;

public interface PublicEventService {
    EventFullDto getEventById(long id, HttpServletRequest request);
}
