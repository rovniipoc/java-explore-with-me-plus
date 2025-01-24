package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.user.dto.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;

    private static final long HOURS_BEFORE_EVENT = 2;

    public List<EventShortDto> getAllEventsOfUser(Long userId, int from, int size) {
        if (size <= 0) {
            throw new ValidationException("Параметр size должен быть больше 0");
        }
        checkUserExists(userId);
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Event> eventPage = eventRepository.findAllByInitiatorId(userId, pageRequest);
        if (eventPage.isEmpty()) {
            throw new NotFoundException("События для пользователя с id=" + userId + " не найдены");
        }
        return eventPage.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());

    }

    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto dto) {
        User initiator = getUserOrThrow(userId);
        Category category = getCategoryOrThrow(dto.getCategory());
        // 1) Лимит участников не должен быть отрицательным → 400 (BadRequestException)
        if (dto.getParticipantLimit() != null && dto.getParticipantLimit() < 0) {
            throw new BadRequestException("Лимит участников не может быть отрицательным");
        }

        // 2) Дата события не должна быть в прошлом и должна быть >= (now + 2 часа) → 400
        if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(HOURS_BEFORE_EVENT))) {
            throw new BadRequestException(
                    "Дата события не может быть раньше, чем через " + HOURS_BEFORE_EVENT + " часа(ов) от текущего момента."
            );
        }
        Event event = EventMapper.toEvent(dto, initiator, category);
        Event saved = eventRepository.save(event);

        return EventMapper.toEventFullDto(saved);
    }

    public EventFullDto getEventOfUser(Long userId, Long eventId) {
        checkUserExists(userId);
        Event event = getEventOrThrow(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Событие не принадлежит пользователю id=" + userId);
        }

        return EventMapper.toEventFullDto(event);

    }

    @Transactional
    public EventFullDto updateEventOfUser(Long userId, Long eventId, UpdateEventUserRequest dto) {
        checkUserExists(userId);
        Event event = getEventOrThrow(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Событие не принадлежит пользователю id=" + userId);
        }
        // Если событие уже опубликовано, а по ТЗ запрещено менять опубликованное → 409 (ValidationException)
        if (EventState.PUBLISHED.equals(event.getState())) {
            throw new ValidationException("Нельзя изменять уже опубликованное событие");
        }

        // Проверяем дату, если она обновляется:
        if (dto.getEventDate() != null && dto.getEventDate().isBefore(LocalDateTime.now().plusHours(HOURS_BEFORE_EVENT))) {
            throw new BadRequestException(
                    "Дата события не может быть раньше, чем через " + HOURS_BEFORE_EVENT + " часа(ов) от текущего момента."
            );
        }

        // Лимит участников опять не должен быть < 0
        if (dto.getParticipantLimit() != null && dto.getParticipantLimit() < 0) {
            throw new BadRequestException("Лимит участников не может быть отрицательным");
        }
        Category category = null;
        if (dto.getCategory() != null) {
            category = getCategoryOrThrow(dto.getCategory());
        }
        EventMapper.updateEventFromUserRequest(event, dto, category);
        Event updated = eventRepository.save(event);

        return EventMapper.toEventFullDto(updated);
    }

    // Вспомогательные методы
    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }

    private Event getEventOrThrow(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + id + " не найдено"));
    }

    private Category getCategoryOrThrow(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id=" + catId + " не найдена"));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    private Long getConfirmedRequests(Long eventId) {
        return requestRepository.countConfirmedRequestsByEventId(eventId);
    }

    private Long getViews(Long eventId) {
        return requestRepository.getViewsForEvent(eventId);
    }
}
