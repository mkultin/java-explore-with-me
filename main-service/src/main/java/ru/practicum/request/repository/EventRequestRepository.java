package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.enums.RequestStatus;
import ru.practicum.request.model.EventRequest;

import java.util.List;

public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {
    List<EventRequest> findByRequestorId(Long requestorId);

    List<EventRequest> findByEventIdAndStatus(Long eventId, RequestStatus status);

    List<EventRequest> findByEventId(Long eventId);
}
