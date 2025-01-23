package ru.practicum.ewm.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.EndpointHitInputDto;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.ViewStatsOutputDto;
import ru.practicum.ewm.event.dto.Event;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventMapper;
import ru.practicum.ewm.event.dto.EventState;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.RequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {
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
        Long countConfirmedRequests = requestRepository.countConfirmedRequestsByEventId(id);
        Long views = getEventViews(id);

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);

        log.info("получен eventFullDto с ID = {}", eventFullDto.getId());
        return eventFullDto;
    }

    private void addHit(HttpServletRequest request) {
        statsClient.addHit(new EndpointHitInputDto("ewm-main", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
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
