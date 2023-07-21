package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.StatMapper;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImpl implements StatService {
    private final StatsRepository repository;

    @Override
    public void save(EndpointHitDto endpointHitDto) {
        log.info("Добавлена запись о запросе uri={} от пользователя с ip={}",
                endpointHitDto.getUri(), endpointHitDto.getIp());
        repository.save(StatMapper.toEndpointHit(endpointHitDto));
    }

    @Override
    public List<ViewStatsDto> find(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        validateTime(start, end);
        List<ViewStatsDto> statsDtos;
        if (unique) {
            statsDtos = repository.findViewStatsWithUnique(start, end, uris);
            log.info("Получена uniq статистика {}", statsDtos);
            return statsDtos;
        }
        statsDtos = repository.findViewStats(start, end, uris);
        log.info("Получена статистика {}", statsDtos);
        return statsDtos;
    }

    private void validateTime(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new ValidationException("Неверные параметры временного отрезка");
        }
    }
}
