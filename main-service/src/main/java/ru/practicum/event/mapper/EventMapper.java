package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.EndpointHitDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.*;
import ru.practicum.event.enums.State;
import ru.practicum.event.model.Event;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {
    public Event toEvent(NewEventDto eventDto, User user, Category category) {
        return Event.builder()
                .title(eventDto.getTitle())
                .annotation(eventDto.getAnnotation())
                .description(eventDto.getDescription())
                .category(category)
                .initiator(user)
                .eventDate(eventDto.getEventDate())
                .createdOn(LocalDateTime.now())
                .location(eventDto.getLocation())
                .paid(eventDto.getPaid())
                .participantLimit(eventDto.getParticipantLimit())
                .requestModeration(eventDto.getRequestModeration())
                .state(State.PENDING)
                .build();
    }

    public EventFullDto toEventFullDto(Event event, Long views, Integer requests) {
        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .initiator(UserShortDto.builder()
                        .id(event.getInitiator().getId())
                        .name(event.getInitiator().getName())
                        .build())
                .eventDate(event.getEventDate())
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .confirmedRequests(requests)
                .views(views)
                .build();
    }

    public EventFullWithAdminCommentDto toEventFullWithAdminCommentDto(Event event, Long views, Integer requests) {
        return EventFullWithAdminCommentDto.EventFullWithAdminCommentDtoBuilder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .initiator(UserShortDto.builder()
                        .id(event.getInitiator().getId())
                        .name(event.getInitiator().getName())
                        .build())
                .eventDate(event.getEventDate())
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .confirmedRequests(requests)
                .views(views)
                .adminComment(event.getAdminComment())
                .build();
    }

    public EventShortDto toEventShortDto(Event event, Long views, Integer requests) {
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .eventDate(event.getEventDate())
                .paid(event.getPaid())
                .confirmedRequests(requests)
                .views(views)
                .build();
    }

    public void updateEvent(EventUpdateDto eventDto, Event eventToUpdate) {
        if (eventDto.getTitle() != null && !eventDto.getTitle().isBlank()) eventToUpdate.setTitle(eventDto.getTitle());
        if (eventDto.getDescription() != null && !eventDto.getDescription().isBlank())
            eventToUpdate.setDescription(eventDto.getDescription());
        if (eventDto.getAnnotation() != null && !eventDto.getAnnotation().isBlank())
            eventToUpdate.setAnnotation(eventDto.getAnnotation());
        if (eventDto.getEventDate() != null) eventToUpdate.setEventDate(eventDto.getEventDate());
        if (eventDto.getLocation() != null && eventDto.getLocation().getLat() != null &&
                eventDto.getLocation().getLon() != null) eventToUpdate.setLocation(eventDto.getLocation());
        if (eventDto.getPaid() != null) eventToUpdate.setPaid(eventDto.getPaid());
        if (eventDto.getParticipantLimit() != null) eventToUpdate.setParticipantLimit(eventDto.getParticipantLimit());
        if (eventDto.getRequestModeration() != null)
            eventToUpdate.setRequestModeration(eventDto.getRequestModeration());
    }

    public EndpointHitDto toEndpointHitDto(HttpServletRequest request) {
        return EndpointHitDto.builder()
                .app("main-service")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
