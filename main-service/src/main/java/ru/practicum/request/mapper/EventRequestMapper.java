package ru.practicum.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.event.model.Event;
import ru.practicum.request.dto.EventRequestDto;
import ru.practicum.request.model.EventRequest;
import ru.practicum.user.model.User;

@UtilityClass
public class EventRequestMapper {
    public EventRequest toEventRequest(EventRequestDto requestDto, User user, Event event) {
        return EventRequest.builder()
                .requestor(user)
                .event(event)
                .created(requestDto.getCreated())
                .status(requestDto.getStatus())
                .build();
    }

    public EventRequestDto toRequestDto(EventRequest eventRequest) {
        return EventRequestDto.builder()
                .id(eventRequest.getId())
                .requester(eventRequest.getRequestor().getId())
                .event(eventRequest.getEvent().getId())
                .created(eventRequest.getCreated())
                .status(eventRequest.getStatus())
                .build();
    }
}
