package ru.practicum.compilation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

@Getter
@ToString
@Builder
public class CompilationDto {
    private Long id;
    private List<EventShortDto> events;
    private Boolean pinned;
    private String title;
}
