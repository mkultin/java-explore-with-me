package ru.practicum.service;

import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    void save(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> find(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
