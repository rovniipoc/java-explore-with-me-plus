package ru.practicum.ewm.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EventSort;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicEventService {
    EventFullDto getEventById(long id, HttpServletRequest request);

    List<EventShortDto> getEvents(String text,
                                  List<Long> categories,
                                  boolean paid,
                                  LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  boolean onlyAvailable,
                                  EventSort sort,
                                  int from,
                                  int size,
                                  HttpServletRequest request);
}
