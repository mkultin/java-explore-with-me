package ru.practicum.request.service;

import ru.practicum.request.dto.EventRequestDto;
import ru.practicum.request.model.EventRequest;

import java.util.List;

public interface EventRequestService {
    EventRequestDto save(Long requestorId, Long eventId);

    EventRequestDto update(Long requestorId, Long eventId);

    List<EventRequestDto> getRequestsByRequestor(Long requestorId);

    List<EventRequest> getRequestsForEvent(Long eventId);

    EventRequest getById(Long requestId);
}
