package ru.practicum.ewm.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.EndpointHitInputDto;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.ViewStatsOutputDto;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private static final String APP_NAME = "ewm-main";

    private final EventRepository eventRepository;
    private final StatsClient statsClient;

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Event c id " + id + "не найден"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event c id " + id + "еще не опубликован");
        }

        addHit(request);

        List<ViewStatsOutputDto> viewStats = getViewStats(event.getPublishedOn().minusSeconds(1), LocalDateTime.now(), List.of(request.getRequestURI()));

        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.save(event));
        if (!viewStats.isEmpty()) {
            eventFullDto.setViews(viewStats.get(0).getHits());
        } else {
            eventFullDto.setViews(0L);
        }

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

        PageRequest page = PageRequest.of(from, size); // создали условие пагинации
        Page<Event> pageEvents;
        if (onlyAvailable) { // достаем события у которых не исчерпан лимит запросов на участие
            pageEvents = eventRepository.findAllByPublicFiltersAndOnlyAvailable(text, categories, paid, start, end, page);
        } else { // или все события
            pageEvents = eventRepository.findAllByPublicFilters(text, categories, paid, start, end, page);
        }

        List<Event> events = pageEvents.getContent(); // собрали в коллекцию

        List<String> eventUris = new ArrayList<>(); // список uri
        for (Event event : events) { // для каждого события
            eventUris.add(request.getRequestURI() + "/" + event.getId()); // добавили в список uri
        }

        List<ViewStatsOutputDto> viewStats = getViewStats(start, end, eventUris); // получили список дтошек статистики

        Map<String, Long> viewsMap = new HashMap<>(); // создали карту статистики
        for (ViewStatsOutputDto stat : viewStats) {
            viewsMap.put(stat.getUri(), stat.getHits()); // для каждой uri создали значение hits
        }

        addHit(request); // добавили новый просмотр

        List<EventShortDto> eventShortDtos = new ArrayList<>(); // создали список для дтошек
        for (Event event : events) { // для каждого события
            EventShortDto dto = EventMapper.toEventShortDto(event); // преобразуем в дтошку
            String eventUri = request.getRequestURI() + "/" + event.getId(); // формируем uri
            dto.setViews(viewsMap.getOrDefault(eventUri, 0L)); // для дтошки устанавливаем количество просмотров
            eventShortDtos.add(dto); // добавляем в список
        }

        if (sort != null) { // сортировка
            if (sort.equals(EventSort.EVENT_DATE)) {
                eventShortDtos.sort(Comparator.comparing(EventShortDto::getEventDate));
            } else {
                eventShortDtos.sort(Comparator.comparing(EventShortDto::getViews, Comparator.reverseOrder()));
            }
        } else {
            eventShortDtos.sort(Comparator.comparing(EventShortDto::getViews, Comparator.reverseOrder()));
        }

        return eventShortDtos;
    }

    private void addHit(HttpServletRequest request) {
        EndpointHitInputDto hit = new EndpointHitInputDto();
        hit.setApp(APP_NAME);
        hit.setUri(request.getRequestURI());
        hit.setIp(request.getRemoteAddr());
        hit.setTimestamp(LocalDateTime.now());
        statsClient.addHit(hit);
    }

    private List<ViewStatsOutputDto> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris) {
        ResponseEntity<Object> uriViewStats = statsClient.getStats(start, end, uris, true);
        List<ViewStatsOutputDto> viewStats = new ArrayList<>();
        if (uriViewStats.getBody() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String json = objectMapper.writeValueAsString(uriViewStats.getBody());
                viewStats = objectMapper.readValue(json, new TypeReference<List<ViewStatsOutputDto>>() {
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Ошибка парсинга JSON");
            }
        }
        return viewStats;
    }
}
