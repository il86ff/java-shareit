package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.EmptyEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private static final String NOT_FOUND_MESSAGE = "пользователь с id = %s не найден...";
    private final UserRepository userRepository;

    public User create(UserDto userDto) {
        log.debug("Попытка создания пользователя {}", userDto);
        if (userDto.getEmail() == null) throw new EmptyEmailException("передан пустой email...");
        isValidNotDuplicateEmail(userDto.getEmail());
        return userRepository.create(userDto);
    }

    public User update(UserDto user, Long id) {
        log.debug("Попытка обновления пользователя {}", user);
        if (user.getEmail() !=null && !getById(id).getEmail().equals(user.getEmail())) {
            isValidNotDuplicateEmail(user.getEmail());
        }
        UserDto userDto = UserMapper.toUserDto(user, getById(id));
        User updatedUser = UserMapper.dtoToUser(userDto);
        updatedUser.setId(id);
        return userRepository.update(updatedUser, id);
    }

    public User getById(Long id) {
        log.debug("Получение данных о пользователе с ID = {}", id);
        if (userRepository.getById(id).isPresent()) {
            return userRepository.getById(id).get();
        } else {
            throw new NotFoundException(String.format(NOT_FOUND_MESSAGE, id));
        }
    }

    public Collection<User> getAllUsers() {
        log.debug("Получение всех пользователей");
        return userRepository.getAllUsers();
    }

    public User deleteUser(Long id) {
        log.debug("Удаление пользователя с ID = {}", id);
        if (userRepository.getById(id).isPresent()) {
            return userRepository.deleteUser(id);
        } else {
            throw new NotFoundException(String.format(NOT_FOUND_MESSAGE, id));
        }
    }

    private boolean isValidNotDuplicateEmail(String email) {
        for (User u : userRepository.getAllUsers()) {
            if (u.getEmail().equals(email))
                throw new DuplicateEmailException(String.format("Пользователь с email = %s уже существует", email));
        }
        return true;
    }
}
