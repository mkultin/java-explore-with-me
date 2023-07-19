package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.NotFoundException;
import ru.practicum.service.CheckPagination;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository repository;
    private final EventService eventService;


    @Override
    @Transactional
    public CompilationDto save(CompilationNewDto newDto) {
        if (newDto.getPinned() == null) newDto.setPinned(false);
        List<Event> events;
        if (newDto.getEvents() == null || newDto.getEvents().isEmpty()) {
            events = Collections.emptyList();
        } else {
            events = eventService.findAllById(newDto.getEvents());
        }
        Compilation compilation = repository.save(CompilationMapper.toCompilation(newDto, events));
        log.info("Создана подборка событиий '{}'", compilation.getTitle());
        return CompilationMapper.toCompilationDto(compilation,
                eventService.toEventShortDtos(compilation.getEvents()));
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        Compilation compilation = getCompilation(compId);
        repository.delete(compilation);
        log.info("Подборка событий удалена: id={}, title={}", compId, compilation.getTitle());
    }

    @Override
    @Transactional
    public CompilationDto update(CompilationNewDto newDto, Long compId) {
        Compilation compToUpdate = getCompilation(compId);
        if (newDto.getEvents() != null) compToUpdate.setEvents(eventService.findAllById(newDto.getEvents()));
        if (newDto.getPinned() != null) compToUpdate.setPinned(newDto.getPinned());
        if (newDto.getTitle() != null) compToUpdate.setTitle(newDto.getTitle());
        Compilation compilation = repository.save(compToUpdate);
        log.info("Подборка событий обновлена: id={}, title={}", compId, compilation.getTitle());
        return CompilationMapper.toCompilationDto(compilation,
                eventService.toEventShortDtos(compilation.getEvents()));
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        PageRequest pageRequest = CheckPagination.getPageByParams(from, size);
        List<Compilation> compilations = repository.findByPinned(pinned, pageRequest);
        log.info("Получены подборки событий: {} подборок.", compilations.size());
        return compilations.stream()
                .map(compilation -> CompilationMapper.toCompilationDto(compilation,
                        eventService.toEventShortDtos(compilation.getEvents())))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationDto(Long compId) {
        Compilation compilation = getCompilation(compId);
        return CompilationMapper.toCompilationDto(compilation, eventService.toEventShortDtos(compilation.getEvents()));
    }

    private Compilation getCompilation(Long compId) {
        Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка id=" + compId + " не существует."));
        log.info("Получена подборка событий: id={}, title={}", compId, compilation.getTitle());
        return compilation;
    }
}
