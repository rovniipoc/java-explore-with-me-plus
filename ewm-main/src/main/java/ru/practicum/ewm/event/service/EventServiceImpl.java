package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.EndpointHitInputDto;
import ru.practicum.ewm.StatsClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final StatsClient statsClient;

    @Override
    public ResponseEntity<Object> getEvents(EndpointHitInputDto endpointHitInputDto, LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        statsClient.addHit(endpointHitInputDto);
        return statsClient.getStats(start, end, uris, unique);
    }

    @Override
    public ResponseEntity<Object> getEventById(long id, EndpointHitInputDto endpointHitInputDto, LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        statsClient.addHit(endpointHitInputDto);
        return statsClient.getStats(start, end, uris, unique);
    }
}
