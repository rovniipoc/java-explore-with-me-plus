package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {

    private final EventService eventService;

    /**
     * GET /users/{userId}/events
     * Получить список событий, добавленных текущим пользователем
     */
    @GetMapping
    public List<EventShortDto> getAllEventsOfUser(@PathVariable Long userId) {
        log.info("GET /users/{}/events -> getAllEventsOfUser", userId);
        return eventService.getAllEventsOfUser(userId);
    }

    /**
     * POST /users/{userId}/events
     * Добавить новое событие
     */
    @PostMapping
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @RequestBody NewEventDto dto) {
        log.info("POST /users/{}/events -> createEvent, dto={}", userId, dto);
        return eventService.createEvent(userId, dto);
    }

    /**
     * GET /users/{userId}/events/{eventId}
     * Получить полную информацию о событии текущего пользователя
     */
    @GetMapping("/{eventId}")
    public EventFullDto getEventOfUser(@PathVariable Long userId,
                                       @PathVariable Long eventId) {
        log.info("GET /users/{}/events/{} -> getEventOfUser", userId, eventId);
        return eventService.getEventOfUser(userId, eventId);
    }

    /**
     * PATCH /users/{userId}/events/{eventId}
     * Изменить событие, добавленное текущим пользователем
     */
    @PatchMapping("/{eventId}")
    public EventFullDto updateEventOfUser(@PathVariable Long userId,
                                          @PathVariable Long eventId,
                                          @RequestBody UpdateEventUserRequest dto) {
        log.info("PATCH /users/{}/events/{} -> updateEventOfUser, dto={}", userId, eventId, dto);
        return eventService.updateEventOfUser(userId, eventId, dto);
    }
}
