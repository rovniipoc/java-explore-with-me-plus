package ru.practicum.ewm.event;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.EndpointHitInputDto;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.event.dto.Event;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventMapper;
import ru.practicum.ewm.event.dto.EventState;
import ru.practicum.ewm.exception.NotFoundException;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {
    private final EventRepository eventRepository;
    private final StatsClient statsClient;

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(long id, EndpointHitInputDto endpointHitInputDto) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Event c id " + id + "не найден"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event c id " + id + "еще не опубликован");
        }

        statsClient.addHit(endpointHitInputDto);

        return null;
    }
}
