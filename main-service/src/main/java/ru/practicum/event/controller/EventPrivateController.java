package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventFullDto save(@PathVariable Long userId,
                             @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Получен POST-запрос к эндпоинту /users/{}/events.", userId);
        return eventService.save(newEventDto, userId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @RequestBody @Valid EventUpdateDto eventUpdateDto) {
        log.info("Получен PATCH-запрос к эндпоинту /users/{}/events/{}.", userId, eventId);
        return eventService.updateEventByInitiator(eventId, eventUpdateDto, userId);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByInitiator(@PathVariable Long userId,
                                            @PathVariable Long eventId) {
        log.info("Получен GET-запрос к эндпоинту /users/{}/events/{}.", userId, eventId);
        return eventService.getEventDtoByInitiator(eventId, userId);
    }

    @GetMapping
    public List<EventShortDto> getEventsByInitiator(@PathVariable Long userId,
                                                    @RequestParam(defaultValue = "0") Integer from,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос к эндпоинту /users/{}/events.", userId);
        return eventService.getEventsByInitiator(userId, from, size);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateReqStatus(@PathVariable Long userId,
                                                          @PathVariable Long eventId,
                                                          @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.info("Получен PATCH-запрос к эндпоинту /users/{}/events/{}/requests. Обновить статусы запросов{}",
                userId, eventId, updateRequest.getRequestIds());
        return eventService.updateRequests(userId, eventId, updateRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<EventRequestDto> getRequests(@PathVariable Long userId,
                                             @PathVariable Long eventId) {
        log.info("Получен GET-запрос к эндпоинту /users/{}/events/{}/requests.", userId, eventId);
        return eventService.getEventRequests(userId, eventId);
    }
}
