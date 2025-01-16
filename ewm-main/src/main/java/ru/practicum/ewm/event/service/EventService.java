package ru.practicum.ewm.event.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import ru.practicum.ewm.EndpointHitInputDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    ResponseEntity<Object> getEvents(EndpointHitInputDto endpointHitInputDto, LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

    ResponseEntity<Object> getEventById(long id, EndpointHitInputDto endpointHitInputDto, LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
