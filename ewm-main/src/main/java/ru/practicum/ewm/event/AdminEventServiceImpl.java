package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.ViewStatsOutputDto;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.request.RequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminEventServiceImpl implements AdminEventService {

    private static final Integer HOURS_BEFORE_EVENT_START = 1;

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    //TODO доделать метод
    @Override
    public List<EventFullDto> findEventByParams(List<Long> userIds, List<EventState> states, List<Long> categoryIds,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd, Long from, Long size) {
        return List.of();
    }

    @Transactional
    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event existEvent = checkEventExist(eventId);

        if (updateEventAdminRequest.getAnnotation() != null) {
            existEvent.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            existEvent.setCategory(categoryRepository.findById(updateEventAdminRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категории c id = " + updateEventAdminRequest.getCategory() + " не существует")));
        }
        if (updateEventAdminRequest.getDescription() != null) {
            existEvent.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            checkIsStartAfterNowPlusHours(updateEventAdminRequest, HOURS_BEFORE_EVENT_START);
            existEvent.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            existEvent.setLocation(updateEventAdminRequest.getLocation());
        }
        if (updateEventAdminRequest.getPaid() != null) {
            existEvent.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            existEvent.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            existEvent.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            setEventState(existEvent, updateEventAdminRequest);
        }
        if (updateEventAdminRequest.getTitle() != null) {
            existEvent.setTitle(updateEventAdminRequest.getTitle());
        }

        Long countConfirmedRequests = requestRepository.countConfirmedRequestsByEventId(eventId);
        Long views = getEventViews(eventId);

        return EventMapper.toEventFullDto(eventRepository.save(existEvent), countConfirmedRequests, views);
    }

    private Event checkEventExist(Long id) {
        Optional<Event> maybeEvent = eventRepository.findById(id);
        if (maybeEvent.isPresent()) {
            return maybeEvent.get();
        } else {
            throw new NotFoundException("События с id = " + id + " не существует");
        }
    }

    private void checkIsStartAfterNowPlusHours(UpdateEventAdminRequest updateEventAdminRequest, Integer hours) {
        if (!updateEventAdminRequest.getEventDate().isAfter(LocalDateTime.now().plusHours(hours))) {
            throw new ValidationException("Дата начала изменяемого события должна быть не ранее чем за " + hours + " час(ов) от даты публикации (текущего времени)");
        }
    }

    private void setEventState(Event event, UpdateEventAdminRequest updateEventAdminRequest) {
        switch (updateEventAdminRequest.getStateAction()) {
            case StateAction.REJECT_EVENT:
                if (event.getState() == EventState.PUBLISHED) {
                    throw new ValidationException("Нельзя отклонить событие, которое находится в статусе опубликовано");
                }
                event.setState(EventState.CANCELED);
                break;
            case StateAction.PUBLISH_EVENT:
                if (event.getState() != EventState.PENDING) {
                    throw new ValidationException("Опубликовать событие можно только если оно находится в статусе ожидания");
                }
                event.setState(EventState.PUBLISHED);
                break;
        }
    }

    private Long getEventViews(Long eventId) {
        String eventUri = "/events/" + eventId;
        List<String> uris = new ArrayList<>();
        uris.add(eventUri);

        Object response = statsClient.getStats(null, null, uris, false);
        if (response instanceof List<?> responseList) {
            if (!responseList.isEmpty() && responseList.getFirst() instanceof ViewStatsOutputDto viewStatsOutputDto) {
                return viewStatsOutputDto.getHits();
            }
        }

        return 0L;
    }


}
