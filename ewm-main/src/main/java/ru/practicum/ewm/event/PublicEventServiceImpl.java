package ru.practicum.ewm.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.EndpointHitInputDto;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.ViewStatsOutputDto;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.RequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private static final String APP_NAME = "ewm-main";

    private final EventRepository eventRepository;
    private final StatsClient statsClient;
    private final RequestRepository requestRepository;

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Event c id " + id + "не найден"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event c id " + id + "еще не опубликован");
        }

        addHit(request);
        updateEventViewsInRepository(event);

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);

        log.info("получен eventFullDto с ID = {}", eventFullDto.getId());
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getEvents(String text,
                                         List<Long> categories,
                                         boolean paid,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         boolean onlyAvailable,
                                         EventSort sort,
                                         int from,
                                         int size,
                                         HttpServletRequest request) {

        LocalDateTime start = rangeStart != null ? rangeStart : LocalDateTime.now(); // устанавливаем начальное значение поиска
        LocalDateTime end = rangeEnd != null ? rangeEnd : LocalDateTime.now().plusYears(1); // устанавливаем время окончания поиска

        if (end.isBefore(start)) { // проверка что окончание не раньше старта
            throw new BadRequestException("Недопустимый временной промежуток, время окончание поиска не может быть раньше времени начала поиска");
        }

        PageRequest page = PageRequest.of(from, size); // создали учловия пагинации
        Page<Event> pageEvents = eventRepository.findAllByPublicFilters(text, categories, paid, start, end, page); // получили евенты

        List<Event> events = pageEvents.getContent(); // собрали в коллекцию

        events.forEach(event -> event.setConfirmedRequests(
            requestRepository.countConfirmedRequestsByEventId(event.getId()))); // установили значение подтвержденных запросов для каждого события

        if(onlyAvailable) { // получили события у которых не исчерпан лимит запросов на участие
            events = events.stream().filter(event -> event.getParticipantLimit() > event.getConfirmedRequests()).toList();
        }

        events.forEach(this::updateEventViewsInRepository); // для каждого события обновили статистику просмотров

        List<EventShortDto> eventsShortDto = events.stream().map(EventMapper::toEventShortDto).toList(); // перевели события в дтошки

        if (sort != null) { // сортировка дтошек
            switch (sort) {
                case EVENT_DATE:
                    eventsShortDto.sort(Comparator.comparing(EventShortDto::getEventDate));
                    break;
                case VIEWS:
                    eventsShortDto.sort(Comparator.comparing(EventShortDto::getViews));
                    break;
            }
        } else {
            eventsShortDto.sort(Comparator.comparing(EventShortDto::getViews));
        }

        addHit(request);

        return eventsShortDto;
    }

    private void addHit(HttpServletRequest request) {
        EndpointHitInputDto hit = new EndpointHitInputDto();
        hit.setApp(APP_NAME);
        hit.setUri(request.getRequestURI());
        hit.setIp(request.getRemoteAddr());
        hit.setTimestamp(LocalDateTime.now());
        statsClient.addHit(hit);
    }

    private Event updateEventViewsInRepository(Event event) {
        Long eventId = event.getId();
        String eventUri = "/events/" + eventId;
        List<String> uris = new ArrayList<>();
        uris.add(eventUri);

        Object response = statsClient.getStats(null, null, uris, false);
        if (response instanceof List<?> responseList) {
            if (!responseList.isEmpty() && responseList.getFirst() instanceof ViewStatsOutputDto viewStatsOutputDto) {
                event.setViews(viewStatsOutputDto.getHits());
                return eventRepository.save(event);
            }
        }

        return event;
    }
}
