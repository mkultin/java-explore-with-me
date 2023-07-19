package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.enums.State;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.dto.EventRequestDto;
import ru.practicum.request.enums.RequestStatus;
import ru.practicum.request.mapper.EventRequestMapper;
import ru.practicum.request.model.EventRequest;
import ru.practicum.request.repository.EventRequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventRequestServiceImpl implements EventRequestService {
    private final EventRequestRepository requestRepository;
    private final UserService userService;
    private final EventService eventService;

    @Transactional
    @Override
    public EventRequestDto save(Long requestorId, Long eventId) {
        if (eventId == null)
            throw new ValidationException("Для создания запроса необходимо указать событие.");
        User requestor = userService.getUserById(requestorId);
        Event event = eventService.getEventById(eventId);
        if (event.getInitiator().equals(requestor))
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии.");
        if (!event.getState().equals(State.PUBLISHED))
            throw new ConflictException("Нельзя участвовать в неопубликованном событии.");
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(getRequestsForEvent(eventId).size()))
            throw new ConflictException("У себытия id={} достиггнут лимит запросов на участие.");
        EventRequestDto requestDto = new EventRequestDto();
        requestDto.setEvent(eventId);
        requestDto.setRequester(requestorId);
        requestDto.setCreated(LocalDateTime.now());
        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            requestDto.setStatus(RequestStatus.PENDING);
        } else {
            requestDto.setStatus(RequestStatus.CONFIRMED);
        }
        EventRequest eventRequest =
                requestRepository.save(EventRequestMapper.toEventRequest(requestDto, requestor, event));
        log.info("Добавлен запрос id={} от пользователя id={} на участие в событии id={}",
                eventRequest.getId(), requestorId, eventId);
        return EventRequestMapper.toRequestDto(eventRequest);
    }

    @Transactional
    @Override
    public EventRequestDto update(Long requestorId, Long requestId) {
        userService.getUserById(requestId);
        EventRequest requestToUpdate = getById(requestId);
        requestToUpdate.setStatus(RequestStatus.CANCELED);
        EventRequest eventRequest = requestRepository.save(requestToUpdate);
        log.info("Запрос id={} отменён.", eventRequest.getId());
        return EventRequestMapper.toRequestDto(eventRequest);
    }

    @Override
    public List<EventRequestDto> getRequestsByRequestor(Long requestorId) {
        userService.getUserById(requestorId);
        List<EventRequest> requests = requestRepository.findByRequestorId(requestorId);
        if (requests.isEmpty()) {
            log.info("У пользователя id={} нет запросов на участие в событиях", requestorId);
            return Collections.emptyList();
        }
        log.info("Получен список запросов пользователя id={}, {} запросов.", requestorId, requests.size());
        return requests.stream()
                .map(EventRequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventRequest> getRequestsForEvent(Long eventId) {
        eventService.getEventById(eventId);
        List<EventRequest> requests = requestRepository.findByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (requests.isEmpty()) {
            log.info("Список запросов на участие в событии id={} пуст или равен 0.", eventId);
            return Collections.emptyList();
        }
        log.info("Получен список запросов на участие в событии id={}: {} подтвержденных запросов.",
                eventId, requests.size());
        return requests;
    }

    @Override
    public EventRequest getById(Long requestId) {
        EventRequest eventRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос id=" + requestId + " не найден."));
        log.info("Получен запрос {}.", eventRequest);
        return eventRequest;
    }
}
