package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.dto.RequestStatus;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
public class PrivateRequestController {

    private final RequestService requestService;

    /**
     * GET /users/{userId}/requests
     * Получить заявки текущего пользователя на участие в чужих событиях
     */
    @GetMapping("/requests")
    public List<ParticipationRequestDto> getRequestsOfUser(@PathVariable Long userId) {
        log.info("GET /users/{}/requests -> getRequestsOfUser", userId);
        return requestService.getRequestsOfUser(userId);
    }

    /**
     * POST /users/{userId}/requests?eventId=1
     * Добавить запрос на участие в событии
     */
    @PostMapping("/requests")
    public ParticipationRequestDto addRequest(@PathVariable Long userId,
                                              @RequestParam Long eventId) {
        log.info("POST /users/{}/requests?eventId={} -> addRequest", userId, eventId);
        return requestService.addRequest(userId, eventId);
    }

    /**
     * PATCH /users/{userId}/requests/{requestId}/cancel
     * Отмена своего запроса на участие
     */
    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        log.info("PATCH /users/{}/requests/{}/cancel -> cancelRequest", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }

    /**
     * GET /users/{userId}/events/{eventId}/requests
     * Получить заявки на событие текущего пользователя
     */
    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsForUserEvent(@PathVariable Long userId,
                                                                 @PathVariable Long eventId) {
        log.info("GET /users/{}/events/{}/requests -> getRequestsForUserEvent", userId, eventId);
        return requestService.getRequestsForUserEvent(userId, eventId);
    }

    /**
     * PATCH /users/{userId}/events/{eventId}/requests
     * Изменить статус заявок (массово)
     * Здесь для упрощения примем, что на вход просто список requestIds и один статус
     * В реальном Swagger приходит EventRequestStatusUpdateRequest
     */
    @PatchMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> changeRequestsStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestParam List<Long> requestIds,
            @RequestParam RequestStatus status
    ) {
        log.info("PATCH /users/{}/events/{}/requests?requestIds={}&status={} -> changeRequestsStatus",
                userId, eventId, requestIds, status);
        return requestService.changeRequestsStatus(userId, eventId, requestIds, status);
    }
}
