package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.service.PaginationUtil;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto save(UserDto userDto) {
        try {
            User user = repository.save(UserMapper.toUser(userDto));
            log.info("Добавлен новый пользователь id={}, name={}", user.getId(), user.getName());
            return UserMapper.toUserDto(user);
        } catch (RuntimeException e) {
            throw new AlreadyExistsException("Пользователь с email=" + userDto.getEmail() + " уже существует.");
        }
    }

    @Override
    public void delete(Long userId) {
        getUserById(userId);
        repository.deleteById(userId);
        log.info("Удален пользователь id={}", userId);
    }

    @Override
    public List<UserDto> get(List<Long> userIds, int from, int size) {
        PageRequest pageRequest = PaginationUtil.getPageByParamsWithSort(from, size, "id");
        List<User> users;
        if (userIds == null) {
            users = repository.findAll(pageRequest).toList();
        } else {
            users = repository.findUsersByIdIn(userIds, pageRequest);
        }
        log.info("Получен список пользователей ({} шт.) по запросу.", users.size());
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public User getUserById(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден."));
        log.info("Получен пользователь name={}, id={}", user.getName(), user.getId());
        return user;
    }
}
