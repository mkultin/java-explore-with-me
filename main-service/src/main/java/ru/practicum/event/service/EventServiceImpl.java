package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.CategoryService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.event.enums.State;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.dto.EventRequestDto;
import ru.practicum.request.enums.RequestStatus;
import ru.practicum.request.mapper.EventRequestMapper;
import ru.practicum.request.model.EventRequest;
import ru.practicum.request.repository.EventRequestRepository;
import ru.practicum.service.PaginationUtil;
import ru.practicum.service.Constants;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository repository;
    private final EventRequestRepository requestRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final StatClientServer statClientServer;

    @Transactional
    @Override
    public EventFullDto save(NewEventDto newEventDto, Long userId) {
        User initiator = userService.getUserById(userId);
        Category category = categoryService.getById(newEventDto.getCategory());
        Event event = repository.save(EventMapper.toEvent(newEventDto, initiator, category));
        log.info("Пользвателем id={} добавлено новое событие id={}, title={}.",
                userId, event.getId(), event.getTitle());
        return EventMapper.toEventFullDto(event, 0L, 0);
    }

    @Override
    public Event getEventById(Long eventId) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие id= " + eventId + "не найдено"));
        log.info("Получено событие id={}, title={}.", eventId, event.getTitle());
        return event;
    }

    @Override
    public EventFullDto getEventDtoByInitiator(Long eventId, Long userId) {
        User initiator = userService.getUserById(userId);
        Event event = getEventById(eventId);
        if (!event.getInitiator().equals(initiator)) throw new ValidationException("Пользователь id=" + userId +
                " не является организатором для события id=" + eventId);
        return toReturnEventDto(event);
    }

    @Override
    public List<EventShortDto> getEventsByInitiator(Long userId, int from, int size) {
        userService.getUserById(userId);
        PageRequest pageRequest = PaginationUtil.getPageByParams(from, size);
        List<Event> events = repository.findEventsByInitiatorIdOrderById(userId, pageRequest);
        log.info("Получен список событий ({} шт.) для пользователя id={}", events.size(), userId);
        if (events.isEmpty()) return Collections.emptyList();
        return toEventShortDtos(events);
    }

    @Transactional
    @Override
    public EventFullDto updateEventByInitiator(Long eventId, EventUpdateDto eventDto, Long userId) {
        User initiator = userService.getUserById(userId);
        Event eventToUpdate = getEventById(eventId);
        if (eventToUpdate.getState().equals(State.PUBLISHED))
            throw new ConflictException("Опубликованное событие не может быть изменено.");
        if (!eventToUpdate.getInitiator().equals(initiator))
            throw new ValidationException("Пользователь id=" + userId + " не является организатором " +
                    "для события id=" + eventId);
        if (eventDto.getEventDate() != null && eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2)))
            throw new ConflictException("Событие не может быть раньше, чем через два часа от текущего момента.");
        if (eventDto.getStateAction() != null) {
            switch (eventDto.getStateAction()) {
                case SEND_TO_REVIEW:
                    eventToUpdate.setState(State.PENDING);
                    break;
                case CANCEL_REVIEW:
                    eventToUpdate.setState(State.CANCELED);
                    break;
                default:
                    throw new ValidationException("Действие " + eventDto.getStateAction() +
                            " для статуса не предусмотрено.");
            }
        }
        if (eventDto.getCategoryId() != null)
            eventToUpdate.setCategory(categoryService.getById(eventDto.getCategoryId()));
        EventMapper.updateEvent(eventDto, eventToUpdate);
        Event event = repository.save(eventToUpdate);
        log.info("Пользвателем id={} обновлено событие id={}, title={}",
                userId, event.getId(), event.getTitle());
        return toReturnEventDto(event);
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<State> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               int from, int size) {
        validateDate(rangeStart, rangeEnd);
        List<Event> events = repository.findEventsForAdmin(users, states, categories, rangeStart, rangeEnd,
                PaginationUtil.getPageByParams(from, size));
        if (events.isEmpty()) {
            log.info("По данным параметрам запроса событий не найдено.");
            return Collections.emptyList();
        }
        log.info("По указанным параметрам запроса найдено {} событий", events.size());
        return events.stream()
                .map(this::toReturnEventDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto updateEventByAdmin(Long eventId, EventUpdateDto eventDto) {
        Event eventToUpdate = getEventById(eventId);
        if (eventToUpdate.getState().equals(State.PUBLISHED))
            throw new ConflictException("Опубликованное событие не может быть изменено.");
        if (eventDto.getEventDate() != null && eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(1)))
            throw new ConflictException("Событие не может быть раньше, чем через два часа от текущего момента.");
        if (eventDto.getStateAction() != null) {
            switch (eventDto.getStateAction()) {
                case PUBLISH_EVENT:
                    if (eventToUpdate.getState().equals(State.PENDING)) {
                        eventToUpdate.setState(State.PUBLISHED);
                    } else {
                        throw new ConflictException("Событие со статусом " + eventToUpdate.getState() +
                                " не подходит для публикации.");
                    }
                    if (eventToUpdate.getAdminComment() != null) eventToUpdate.setAdminComment(null);
                    break;
                case REJECT_EVENT:
                    eventToUpdate.setState(State.CANCELED);
                    break;
                case NEED_EDITS:
                    eventToUpdate.setState(State.EDITS_PENDING);
                    if (eventDto.getAdminComment() != null) {
                        eventToUpdate.setAdminComment(eventDto.getAdminComment());
                    } else {
                        throw new ValidationException("Для перевода события в статус 'EDITS_PENDING' необходимо " +
                                "наличие коммментария от администратора.");
                    }
                    break;
                default:
                    throw new ValidationException("Действие " + eventDto.getStateAction() +
                            " для статуса не предусмотрено.");
            }
        }
        if (eventDto.getCategoryId() != null)
            eventToUpdate.setCategory(categoryService.getById(eventDto.getCategoryId()));
        EventMapper.updateEvent(eventDto, eventToUpdate);
        Event event = repository.save(eventToUpdate);
        log.info("Адмиистратором обновлено событие id={}, title={}", event.getId(), event.getTitle());
        return toReturnEventDto(event);
    }

    @Override
    public List<EventShortDto> getEventsForPublic(String text, List<Long> categories, Boolean paid,
                                                  LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                  Boolean onlyAvailable, String sort, int from, int size,
                                                  HttpServletRequest request) {
        validateDate(rangeStart, rangeEnd);
        if (rangeStart == null && rangeEnd == null) {
            rangeStart = LocalDateTime.now();
            rangeEnd = LocalDateTime.now().plusYears(10);
        }
        if (text != null) text = text.toLowerCase();
        List<Event> events = repository.findEventsForPublic(State.PUBLISHED, text, categories, paid,
                rangeStart, rangeEnd, PaginationUtil.getPageByParamsWithSortParam(from, size, sort));
        if (!onlyAvailable) {
            log.info("Получен список всех опубликованных событий с учетом фильтров: {} событий",
                    events.size());
            statClientServer.saveStats(EventMapper.toEndpointHitDto(request));
            return toEventShortDtos(events);
        }
        List<EventShortDto> eventShortDtos = events.stream()
                .filter(event -> (event.getParticipantLimit() == 0 ||
                        event.getParticipantLimit() > getConfirmedRequests(event.getId())))
                .map(event -> EventMapper.toEventShortDto(event, getViews(event), getConfirmedRequests(event.getId())))
                .collect(Collectors.toList());
        log.info("Получен список всех опубликованных событий с учетом фильтров: {} событий",
                eventShortDtos.size());
        statClientServer.saveStats(EventMapper.toEndpointHitDto(request));
        return eventShortDtos;
    }

    @Override
    public EventFullDto getPublishedEvent(Long eventId, HttpServletRequest request) {
        Event event = getEventById(eventId);
        if (!event.getState().equals(State.PUBLISHED)) throw new NotFoundException("Для просмотра доступны " +
                "только опубликованные события");
        statClientServer.saveStats(EventMapper.toEndpointHitDto(request));
        return EventMapper.toEventFullDto(event, getViews(event), getConfirmedRequests(eventId));
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequests(Long requestorId, Long eventId,
                                                         EventRequestStatusUpdateRequest statusUpdateRequest) {
        User requestor = userService.getUserById(requestorId);
        Event event = getEventById(eventId);
        if (!event.getInitiator().equals(requestor))
            throw new ValidationException("Пользователь id=" + requestorId +
                    " не является организатором события id=" + eventId);
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0)
            throw new ValidationException("Подтверждение заявок не требуется для событий с отключенной пре-модерацией " +
                    "и событий, для которых нет ограничения на количество участников.");
        if (event.getParticipantLimit() <= getConfirmedRequests(event.getId()))
            throw new ConflictException("Достигнут лимит для количества участников мероприятия.");
        boolean isAvailable = statusUpdateRequest.getStatus().equals(RequestStatus.CONFIRMED);
        EventRequestStatusUpdateResult requestStatusUpdateResult = new EventRequestStatusUpdateResult();
        List<EventRequest> requests = requestRepository.findAllById(statusUpdateRequest.getRequestIds());
        requests.forEach(request -> log.info("Получен запрос id={}.", request.getId()));
        for (EventRequest request : requests) {
            if (!request.getStatus().equals(RequestStatus.PENDING))
                throw new ConflictException("Статус можно изменить только у заявок, находящихся в состоянии ожидания.");
            if (isAvailable) {
                request.setStatus(RequestStatus.CONFIRMED);
                if (event.getParticipantLimit() <= getConfirmedRequests(event.getId())) isAvailable = false;
            } else {
                request.setStatus(RequestStatus.REJECTED);
            }
        }
        requestRepository.saveAll(requests).forEach(request -> {
            log.info("Обновлён статус для заявки id={}: {}", request.getId(), request.getStatus());
            if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
                requestStatusUpdateResult.getConfirmedRequests().add(EventRequestMapper.toRequestDto(request));
            } else {
                requestStatusUpdateResult.getRejectedRequests().add(EventRequestMapper.toRequestDto(request));
            }
        });
        return requestStatusUpdateResult;
    }

    @Override
    public List<EventRequestDto> getEventRequests(Long requestorId, Long eventId) {
        User requestor = userService.getUserById(requestorId);
        Event event = getEventById(eventId);
        if (!event.getInitiator().equals(requestor))
            throw new ValidationException("Пользователь id=" + requestorId +
                    " не является организатором события id=" + eventId);
        List<EventRequest> requests = requestRepository.findByEventId(eventId);
        if (requests.isEmpty()) {
            log.info("Список запросов на участие в событии id={} пуст или равен 0.", eventId);
            return Collections.emptyList();
        }
        log.info("Получен список запросов на участие в событии id={}: {} запросов.",
                eventId, requests.size());
        return requests.stream()
                .map(EventRequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findAllById(List<Long> ids) {
        List<Event> events = repository.findAllById(ids);
        log.info("Получен список событий: {} событий", events.size());
        return events;
    }

    @Override
    public List<EventShortDto> toEventShortDtos(List<Event> events) {
        return events.stream()
                .map(event -> EventMapper.toEventShortDto(event, getViews(event), getConfirmedRequests(event.getId())))
                .collect(Collectors.toList());
    }

    private EventFullDto toReturnEventDto(Event event) {
        if (event.getAdminComment() != null) {
            return EventMapper.toEventFullWithAdminCommentDto(event, getViews(event),
                    getConfirmedRequests(event.getId()));
        }
        return EventMapper.toEventFullDto(event, getViews(event),
                getConfirmedRequests(event.getId()));
    }

    private Long getViews(Event event) {
        return statClientServer.getViews(event.getCreatedOn().format(DateTimeFormatter.ofPattern(Constants.PATTERN)),
                event.getId());
    }

    private Integer getConfirmedRequests(Long eventId) {
        List<EventRequest> requests = requestRepository.findByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (requests.isEmpty()) {
            log.info("Список запросов на участие в событии id={} пуст или равен 0.", eventId);
            return 0;
        }
        log.info("Получен список запросов на участие в событии id={}: {} подтвержденных запросов.",
                eventId, requests.size());
        return requests.size();
    }

    private void validateDate(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && start.isAfter(end))
            throw new ValidationException("Конец временного отрезка не может быть раньше начала");
    }
}
