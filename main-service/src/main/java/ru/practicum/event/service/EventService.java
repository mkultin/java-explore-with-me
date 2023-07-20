package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.event.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.event.enums.State;
import ru.practicum.event.model.Event;
import ru.practicum.request.dto.EventRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto save(NewEventDto newEventDto, Long userId);

    Event getEventById(Long eventId);

    EventFullDto getEventDtoByInitiator(Long eventId, Long userId);

    List<EventShortDto> getEventsByInitiator(Long userId, int from, int size);

    EventFullDto updateEventByInitiator(Long eventId, EventUpdateDto eventDto, Long userId);

    List<EventFullDto> getEventsByAdmin(List<Long> users, List<State> states, List<Long> categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                        int from, int size);

    EventFullDto updateEventByAdmin(Long eventId, EventUpdateDto eventDto);

    List<EventShortDto> getEventsForPublic(String text, List<Long> categories, Boolean paid,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                           Boolean onlyAvailable, String sort, int from, int size,
                                           HttpServletRequest request);

    EventFullDto getPublishedEvent(Long eventId, HttpServletRequest request);

    EventRequestStatusUpdateResult updateRequests(Long requestorId, Long eventId,
                                                  EventRequestStatusUpdateRequest statusUpdateRequest);

    List<EventRequestDto> getEventRequests(Long requestorId, Long eventId);

    List<Event> findAllById(List<Long> ids);

    List<EventShortDto> toEventShortDtos(List<Event> events);
}
