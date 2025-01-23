package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.request.RequestService;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {

    private final EventService eventService;
    private final RequestService requestService;


    @GetMapping
    public ResponseEntity<List<EventShortDto>> getAllEventsOfUser(@PathVariable Long userId,
                                                                  @RequestParam(defaultValue = "0") int from,
                                                                  @RequestParam(defaultValue = "10") int size) {
        log.info("[GET] Получение событий пользователя с ID {} (from={}, size={})", userId, from, size);
        List<EventShortDto> events = eventService.getAllEventsOfUser(userId, from, size);
        return ResponseEntity.ok(events);
    }


    @PostMapping
    public ResponseEntity<EventFullDto> createEvent(@PathVariable Long userId,
                                                    @RequestBody NewEventDto dto) {
        log.info("[POST] Создание события пользователем с ID {}: {}", userId, dto);
        EventFullDto createdEvent = eventService.createEvent(userId, dto);
        return ResponseEntity.status(201).body(createdEvent);
    }


    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getEventOfUser(@PathVariable Long userId,
                                                       @PathVariable Long eventId) {
        log.info("[GET] Получение события с ID {} пользователя {}", userId, eventId);
        EventFullDto event = eventService.getEventOfUser(userId, eventId);
        return ResponseEntity.ok(event);
    }


    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEventOfUser(@PathVariable Long userId,
                                                          @PathVariable Long eventId,
                                                          @RequestBody UpdateEventUserRequest dto) {
        log.info("[PATCH] Изменение события с ID {} пользователя {}: {}", userId, eventId, dto);
        EventFullDto updatedEvent = eventService.updateEventOfUser(userId, eventId, dto);
        return ResponseEntity.ok(updatedEvent);
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequestsForUserEvent(@PathVariable Long userId,
                                                                                 @PathVariable Long eventId) {
        log.info("[GET] Запросы на участие для события {} пользователя {}", eventId, userId);
        List<ParticipationRequestDto> requests = requestService.getRequestsForUserEvent(userId, eventId);
        return ResponseEntity.ok(requests);
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> changeRequestsStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest statusUpdateRequest) {
        log.info("[PATCH] Обновление статусов запросов для события {} пользователя {}: {}",
                userId, eventId, statusUpdateRequest);
        EventRequestStatusUpdateResult result = requestService.changeRequestsStatus(userId, eventId, statusUpdateRequest);
        return ResponseEntity.ok(result);
    }
}
