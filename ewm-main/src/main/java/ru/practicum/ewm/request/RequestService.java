package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.dto.Event;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.request.dto.ParticipationRequest;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.dto.RequestMapper;
import ru.practicum.ewm.request.dto.RequestStatus;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.user.dto.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    /**
     * Получить все заявки текущего пользователя на чужие события
     */
    public List<ParticipationRequestDto> getRequestsOfUser(Long userId) {
        checkUserExists(userId);
        List<ParticipationRequest> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    /**
     * Создать новую заявку на участие в событии (POST /users/{userId}/requests?eventId=...)
     */
    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        User user = getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);


        if (event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Инициатор события не может добавить запрос на участие в своём событии.");
        }
        // - Событие должно быть опубликовано, иначе ошибка
        if (!event.getState().equals(ru.practicum.ewm.event.dto.EventState.PUBLISHED)) {
            throw new ValidationException("Нельзя участвовать в неопубликованном событии.");
        }

        ParticipationRequest request = new ParticipationRequest();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(user);
        request.setStatus(RequestStatus.PENDING);


        if (!event.isRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        ParticipationRequest saved = requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(saved);
    }

    /**
     * Отменить свою заявку (PATCH /users/{userId}/requests/{requestId}/cancel)
     */
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        checkUserExists(userId);
        ParticipationRequest request = getRequestOrThrow(requestId);
        if (!request.getRequester().getId().equals(userId)) {
            throw new NotFoundException("Заявка не принадлежит пользователю id=" + userId);
        }
        request.setStatus(RequestStatus.CANCELED);
        requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(request);
    }

    /**
     * Получить заявки на событие текущего пользователя (GET /users/{userId}/events/{eventId}/requests)
     */
    public List<ParticipationRequestDto> getRequestsForUserEvent(Long userId, Long eventId) {
        checkUserExists(userId);
        Event event = getEventOrThrow(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Событие не принадлежит пользователю id=" + userId);
        }
        List<ParticipationRequest> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    /**
     * Подтвердить/отклонить несколько заявок (PATCH /users/{userId}/events/{eventId}/requests)
     * В swagger это приходит как EventRequestStatusUpdateRequest (requestIds, status)
     */
    @Transactional
    public List<ParticipationRequestDto> changeRequestsStatus(Long userId, Long eventId,
                                                              List<Long> requestIds,
                                                              RequestStatus newStatus) {
        checkUserExists(userId);
        Event event = getEventOrThrow(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Событие не принадлежит пользователю id=" + userId);
        }


        List<ParticipationRequest> requests = requestRepository.findAllById(requestIds);

        for (ParticipationRequest request : requests) {
            //Пример
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ValidationException("Можно менять статус только у заявок в состоянии PENDING");
            }
            request.setStatus(newStatus);
        }
        requestRepository.saveAll(requests);
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));
    }

    private ParticipationRequest getRequestOrThrow(Long id) {
        Optional<ParticipationRequest> optional = requestRepository.findById(id);
        if (optional.isEmpty()) {
            throw new NotFoundException("Заявка с id=" + id + " не найдена");
        }
        return optional.get();
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }
}
