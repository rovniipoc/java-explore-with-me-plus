package ru.practicum.ewm;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticServiceImpl implements StatisticService {

    private final EndpointHitRepository endpointHitRepository;

    @Override
    @Transactional
    public void createEndpointHit(EndpointHitInputDto endpointHitInputDto) {
        endpointHitRepository.save(EndpointHitMapper.toEndpointHit(endpointHitInputDto));
    }

    @Override
    public List<ViewStatsOutputDto> getStats(List<String> uris, LocalDateTime start, LocalDateTime end, Boolean unique) {
        if (unique) {
            return endpointHitRepository.findDistinctIpStats(uris, start, end);
        } else {
            return ViewStatsOutputDtoMapper.MapToViewStatsOutputDto(endpointHitRepository.findStats(uris, start, end));
        }
    }

}
