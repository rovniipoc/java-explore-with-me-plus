package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.EndpointHitInputDto;
import ru.practicum.ewm.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EventController {
    private final EventService service;

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> searchEvents(@Validated @RequestBody EndpointHitInputDto endpointHitInputDto,
                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                               @RequestParam(required = false) List<String> uris,
                                               @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        return service.getEvents(endpointHitInputDto, start, end, uris, unique);
    }

    @GetMapping("/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getEventById(@PathVariable long id,
                                               @Validated @RequestBody EndpointHitInputDto endpointHitInputDto,
                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                               @RequestParam(required = false) List<String> uris,
                                               @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        return service.getEventById(id, endpointHitInputDto, start, end, uris, unique);
    }
}
