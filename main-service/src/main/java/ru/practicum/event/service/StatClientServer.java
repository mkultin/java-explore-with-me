package ru.practicum.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatClient;
import ru.practicum.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatClientServer {
    private final StatClient statClient;
    private final ObjectMapper mapper;
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    public Long getViews(String start, Long id) {
        ResponseEntity<Object> response = statClient.getStats(
                start,
                LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ofPattern(PATTERN)),
                "/events/" + id, true);
        List<ViewStatsDto> statsDtos = mapper.convertValue(response.getBody(), new TypeReference<>() {
        });
        if (statsDtos == null || statsDtos.size() == 0) return 0L;
        return statsDtos.get(0).getHits();
    }

    public ResponseEntity<Object> saveStats(EndpointHitDto endpointHitDto) {
         return statClient.save(endpointHitDto);
    }


}
