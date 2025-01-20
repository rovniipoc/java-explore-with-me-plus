package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.user.dto.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Получение всех событий пользователя (короткая форма)
     */
    public List<EventShortDto> getAllEventsOfUser(Long userId) {

        checkUserExists(userId);

        List<Event> events = eventRepository.findAllByInitiatorId(userId);
        // Заглушки для confirmedRequests / views (например, 0L).
        return events.stream()
                .map(e -> EventMapper.toEventShortDto(e, 0L, 0L))
                .collect(Collectors.toList());
    }

    /**
     * Создание нового события
     */
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto dto) {
        User initiator = getUserOrThrow(userId);
        Category category = getCategoryOrThrow(dto.getCategory());

        Event event = EventMapper.toEvent(dto, initiator, category);
        Event saved = eventRepository.save(event);
        // confirmedRequests / views = 0 по умолчанию
        return EventMapper.toEventFullDto(saved, 0L, 0L);
    }

    /**
     * Получение полной информации о событии текущего пользователя
     */
    public EventFullDto getEventOfUser(Long userId, Long eventId) {
        checkUserExists(userId);
        Event event = getEventOrThrow(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Событие не принадлежит пользователю id=" + userId);
        }
        // confirmedRequests / views = 0 (заглушка)
        return EventMapper.toEventFullDto(event, 0L, 0L);
    }

    /**
     * Обновление события (пользователем)
     */
    @Transactional
    public EventFullDto updateEventOfUser(Long userId, Long eventId, UpdateEventUserRequest dto) {
        checkUserExists(userId);
        Event event = getEventOrThrow(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Событие не принадлежит пользователю id=" + userId);
        }
        // Если в dto есть поле category — загружаем новую категорию
        Category category = null;
        if (dto.getCategory() != null) {
            category = getCategoryOrThrow(dto.getCategory());
        }

        // Применяем обновления
        EventMapper.updateEventFromUserRequest(event, dto, category);

        // Сохраняем
        Event updated = eventRepository.save(event);
        return EventMapper.toEventFullDto(updated, 0L, 0L);
    }

    // Вспомогательные методы
    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }

    private Event getEventOrThrow(Long id) {
        Optional<Event> optional = eventRepository.findById(id);
        if (optional.isEmpty()) {
            throw new NotFoundException("Событие с id=" + id + " не найдено");
        }
        return optional.get();
    }

    private Category getCategoryOrThrow(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id=" + catId + " не найдена"));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }
}
