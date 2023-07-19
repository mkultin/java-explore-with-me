package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.service.Marker;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CompilationDto save(@RequestBody @Validated(Marker.Create.class) CompilationNewDto compilationNewDto) {
        log.info("Получен POST-запрос к эндпоинту /admin/compilations");
        return compilationService.save(compilationNewDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long compId) {
        log.info("Получен DELETE-запрос к эндпоинту /admin/compilations/{}", compId);
        compilationService.delete(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto update(@RequestBody @Validated(Marker.Update.class) CompilationNewDto compilationNewDto,
                                 @PathVariable Long compId) {
        log.info("Получен PATCH-запрос к эндпоинту /admin/compilations/{}", compId);
        return compilationService.update(compilationNewDto, compId);
    }
}
