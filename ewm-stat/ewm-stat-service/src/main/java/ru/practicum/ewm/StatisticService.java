package ru.practicum.ewm;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticService {

    void createEndpointHit(EndpointHitInputDto endpointHitInputDto);

    List<ViewStatsOutputDto> getStats(List<String> uris, LocalDateTime start, LocalDateTime end, Boolean unique);
}
