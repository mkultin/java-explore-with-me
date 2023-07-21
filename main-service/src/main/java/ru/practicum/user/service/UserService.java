package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {
    UserDto save(UserDto userDto);

    void delete(Long userId);

    List<UserDto> get(List<Long> userIds, int from, int size);

    User getUserById(Long userId);
}
