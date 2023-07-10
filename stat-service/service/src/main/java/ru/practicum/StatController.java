package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.StatService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatController {
    private final StatService service;
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    @PostMapping(path = "/hit")
    @ResponseStatus(value = HttpStatus.CREATED)
    void save(@RequestBody EndpointHitDto endpointHitDto) {
        service.save(endpointHitDto);
    }

    @GetMapping(path = "/stats")
    List<ViewStatsDto> getStats(@RequestParam @DateTimeFormat(pattern = PATTERN) LocalDateTime start,
                                @RequestParam @DateTimeFormat(pattern = PATTERN) LocalDateTime end,
                                @RequestParam(required = false) List<String> uris,
                                @RequestParam(defaultValue = "false") Boolean unique) {
        return service.find(start, end, uris, unique);
    }
}
