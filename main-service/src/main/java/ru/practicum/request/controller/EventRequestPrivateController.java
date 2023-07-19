package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.EventRequestDto;
import ru.practicum.request.service.EventRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
public class EventRequestPrivateController {
    private final EventRequestService requestService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventRequestDto save(@PathVariable Long userId,
                                @RequestParam(required = false) Long eventId) {
        log.info("Получен POST-запрос к эндпоинту /users/{}/requests.", userId);
        return requestService.save(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public EventRequestDto update(@PathVariable Long userId,
                                  @PathVariable Long requestId) {
        log.info("Получен PATCH-запрос к эндпоинту /users/{}/requests/{}/cancel.", userId, requestId);
        return requestService.update(userId, requestId);
    }

    @GetMapping
    public List<EventRequestDto> getRequests(@PathVariable Long userId) {
        log.info("Получен GET-запрос к эндпоинту /users/{}/requests.", userId);
        return requestService.getRequestsByRequestor(userId);
    }
}
