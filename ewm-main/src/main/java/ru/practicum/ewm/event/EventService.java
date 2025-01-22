package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.user.dto.User;

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


    public List<EventShortDto> getAllEventsOfUser(Long userId, int from, int size) {

        checkUserExists(userId);
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Event> eventPage = eventRepository.findAllByInitiatorId(userId, pageRequest);

        return eventPage.stream()
                .map(e -> EventMapper.toEventShortDto(
                        e,
                        getConfirmedRequests(e.getId()),
                        getViews(e.getId())
                ))
                .collect(Collectors.toList());

    }

    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto dto) {
        User initiator = getUserOrThrow(userId);
        Category category = getCategoryOrThrow(dto.getCategory());

        Event event = EventMapper.toEvent(dto, initiator, category);
        Event saved = eventRepository.save(event);
        Long confirmedRequests = getConfirmedRequests(saved.getId());
        Long views = getViews(saved.getId());

        return EventMapper.toEventFullDto(saved, confirmedRequests, views);
    }

    public EventFullDto getEventOfUser(Long userId, Long eventId) {
        checkUserExists(userId);
        Event event = getEventOrThrow(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Событие не принадлежит пользователю id=" + userId);
        }
        Long confirmedRequests = getConfirmedRequests(event.getId());
        Long views = getViews(event.getId());

        return EventMapper.toEventFullDto(event, confirmedRequests, views);

    }

    @Transactional
    public EventFullDto updateEventOfUser(Long userId, Long eventId, UpdateEventUserRequest dto) {
        checkUserExists(userId);
        Event event = getEventOrThrow(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Событие не принадлежит пользователю id=" + userId);
        }
        Category category = null;
        if (dto.getCategory() != null) {
            category = getCategoryOrThrow(dto.getCategory());
        }
        EventMapper.updateEventFromUserRequest(event, dto, category);
        Event updated = eventRepository.save(event);
        Long confirmedRequests = getConfirmedRequests(event.getId());
        Long views = getViews(event.getId());

        return EventMapper.toEventFullDto(updated, confirmedRequests, views);
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
