package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Slf4j
public class UserAdminController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        log.info("Полчен POST-запрос на добавление пользователя name={}", userDto.getName());
        return userService.save(userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        log.info("Получен DELETE-запрос на удаление пользователя id={}", userId);
        userService.delete(userId);
    }

    @GetMapping
    public List<UserDto> get(@RequestParam(required = false) List<Long> ids,
                          @RequestParam(defaultValue = "0") Integer from,
                          @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос на получение списка пользователей ids={}", ids);
        return userService.get(ids,from, size);
    }
}
