package ru.practicum.ewm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    // Получение статистики по посещениям (без учета уникальности ip)
    @Query("""
            select new ru.practicum.ewm.ViewStatsOutputDto(eh.app, eh.uri, count(eh.ip))
            from EndpointHit as eh
            where (:uris is null or eh.uri in :uris)
            and eh.timestamp between :start and :end
            group by eh.app, eh.uri
            order by count(eh.ip) desc
            """)
    List<ViewStatsOutputDto> findStats(@Param("uris") List<String> uris,
                                       @Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end);

    // Получение статистики по посещениям (учитываются только уникальные посещения по ip)
    @Query("""
            select new ru.practicum.ewm.ViewStatsOutputDto(eh.app, eh.uri, count(distinct eh.ip))
            from EndpointHit as eh
            where (:uris is null or eh.uri in :uris)
            and eh.timestamp between :start and :end
            group by eh.app, eh.uri
            order by count(distinct eh.ip) desc
            """)
    List<ViewStatsOutputDto> findDistinctIpStats(@Param("uris") List<String> uris,
                                                 @Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);

}