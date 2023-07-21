package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;

import java.util.List;

public interface CompilationService {
    CompilationDto save(CompilationNewDto newDto);

    void delete(Long compId);

    CompilationDto update(CompilationNewDto newDto, Long compId);

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilationDto(Long compId);
}
