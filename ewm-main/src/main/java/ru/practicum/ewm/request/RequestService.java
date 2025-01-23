package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.dto.Event;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.request.dto.*;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.user.dto.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public List<ParticipationRequestDto> getRequestsOfUser(Long userId) {
        checkUserExists(userId);
        List<ParticipationRequest> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)//toParticipationRequestDto
                .collect(Collectors.toList());
    }


    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        User user = getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);


        if (event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Инициатор события не может добавить запрос на участие в своём событии.");
        }

        if (!event.getState().equals(ru.practicum.ewm.event.dto.EventState.PUBLISHED)) {
            throw new ValidationException("Нельзя участвовать в неопубликованном событии.");
        }
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ValidationException("Нельзя повторно подавать заявку на то же событие.");
        }

        ParticipationRequest request = new ParticipationRequest();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(user);
        request.setStatus(event.isRequestModeration() ? RequestStatus.PENDING : RequestStatus.CONFIRMED);

        ParticipationRequest savedRequest = requestRepository.save(request);

        updateConfirmedRequests(eventId);
        return RequestMapper.toParticipationRequestDto(savedRequest);
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {

        ParticipationRequest request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Заявка не найдена или не принадлежит пользователю."));

        request.setStatus(RequestStatus.CANCELED);
        ParticipationRequest updatedRequest = requestRepository.save(request);

        updateConfirmedRequests(request.getEvent().getId());

        return RequestMapper.toParticipationRequestDto(updatedRequest);

    }

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


    @Transactional
    public EventRequestStatusUpdateResult changeRequestsStatus(Long userId, Long eventId,
                                                               EventRequestStatusUpdateRequest statusUpdateRequest) {
        checkUserExists(userId);
        Event event = getEventOrThrow(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Событие не принадлежит пользователю id=" + userId);
        }

        List<Long> requestIds = statusUpdateRequest.getRequestIds();

        List<ParticipationRequest> requests = requestRepository.findAllById(requestIds);

        for (ParticipationRequest request : requests) {

            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ValidationException("Можно менять статус только у заявок в состоянии PENDING");
            }
            request.setStatus(statusUpdateRequest.getStatus());
        }
        requestRepository.saveAll(requests);
        updateConfirmedRequests(eventId);
        List<ParticipationRequestDto> confirmedRequests = requests.stream()
                .filter(r -> r.getStatus() == RequestStatus.CONFIRMED)
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());

        List<ParticipationRequestDto> rejectedRequests = requests.stream()
                .filter(r -> r.getStatus() == RequestStatus.REJECTED)
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    @Transactional
    public void updateConfirmedRequests(Long eventId) {
        Long confirmedRequests = requestRepository.countConfirmedRequestsByEventId(eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));
        event.setConfirmedRequests(confirmedRequests);
        eventRepository.save(event);
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


    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }
}
