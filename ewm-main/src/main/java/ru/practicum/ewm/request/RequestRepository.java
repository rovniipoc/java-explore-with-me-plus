package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.request.dto.ParticipationRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    // Все заявки конкретного пользователя
    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    // Все заявки на конкретное событие
    List<ParticipationRequest> findAllByEventId(Long eventId);
}
