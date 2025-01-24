package ru.practicum.ewm.event;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
import java.util.List;
import java.util.Map;

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

        return eventFullDto;
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

        try {
            Long eventId = event.getId();
            String eventUri = "/events/" + eventId;
            ResponseEntity<Object> responseEntity = statsClient.getStats(null, null, List.of(eventUri), true);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                List<Map<String, Object>> responseBody = objectMapper.convertValue(responseEntity.getBody(), new TypeReference<List<Map<String, Object>>>() {
                });

                List<ViewStatsOutputDto> responseList = responseBody.stream()
                        .map(map -> new ViewStatsOutputDto((String) map.get("app"), (String) map.get("uri"), ((Number) map.get("hits")).longValue()))
                        .toList();

                if (!responseList.isEmpty()) {
                    ViewStatsOutputDto viewStatsOutputDto = responseList.getFirst();
                    event.setViews(viewStatsOutputDto.getHits());
                    return eventRepository.save(event);
                }
            }
            return event;
        } catch (Exception e) {
            return event;
        }
    }
}
