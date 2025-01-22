package ru.practicum.ewm.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.event.dto.Event;

import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {


    Set<Event> findByIdIn(Set<Long> ids);

    Page<Event> findAllByInitiatorId(Long userId, Pageable pageable);

}
