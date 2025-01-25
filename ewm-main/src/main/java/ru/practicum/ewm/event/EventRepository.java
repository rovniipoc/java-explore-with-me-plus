package ru.practicum.ewm.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.event.dto.Event;
import ru.practicum.ewm.event.dto.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {


    Set<Event> findByIdIn(Set<Long> ids);

    Page<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    boolean existsByCategoryId(Long categoryId);

    @Query("""
            select e from Event e
            where (coalesce(:userIds, null) is null or e.initiator.id in :userIds)
            and (coalesce(:states, null) is null or e.state in :states)
            and (coalesce(:categoryIds, null) is null or e.category.id in :categoryIds)
            and (coalesce(:rangeStart, null) is null or e.eventDate >= :rangeStart)
            and (coalesce(:rangeEnd, null) is null or e.eventDate >= :rangeEnd)
            order by e.id desc
            """)
    Page<Event> findByParams(
            @Param("userIds") List<Long> userIds,
            @Param("states") List<EventState> states,
            @Param("categoryIds") List<Long> categoryIds,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable);

}
