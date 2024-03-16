package ru.practicum.shareit.server.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.shareit.server.exception.DuplicateEmailException;
import ru.practicum.shareit.server.exception.EmptyEmailException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.entity.User;
import ru.practicum.shareit.server.user.mapper.UserMapper;
import ru.practicum.shareit.server.user.repository.UserRepository;

import javax.validation.ConstraintViolationException;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private static final String NOT_FOUND_MESSAGE = "пользователь с id = %s не найден...";
    private final UserRepository userRepository;

    @Transactional
    public User create(@RequestBody UserDto userDto) {
        log.debug("Попытка создания пользователя {}", userDto);
        if (userDto.getEmail() == null) throw new EmptyEmailException("передан пустой email...");
        try {
            User newUser = UserMapper.dtoToUser(userDto);
            return userRepository.save(newUser);
        } catch (ConstraintViolationException | NullPointerException | DataIntegrityViolationException s) {
            throw new DuplicateEmailException(String.format("Не верный email у пользователя %s", userDto.getId()));
        }
    }

    @Transactional
    public User update(UserDto user, Long id) {
        log.debug("Попытка обновления пользователя {}", user);
        if (user.getEmail() != null && !getById(id).getEmail().equals(user.getEmail())) {
            isValidNotDuplicateEmail(user.getEmail());
        }
        UserDto userDto = UserMapper.toUserDto(user, getById(id));
        User updatedUser = UserMapper.dtoToUser(userDto);
        updatedUser.setId(id);
        return userRepository.save(updatedUser);
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        log.debug("Получение данных о пользователе с ID = {}", id);
        if (userRepository.findById(id).isPresent()) {
            return userRepository.findById(id).get();
        } else {
            throw new NotFoundException(String.format(NOT_FOUND_MESSAGE, id));
        }
    }

    @Transactional(readOnly = true)
    public Collection<User> getAllUsers() {
        log.debug("Получение всех пользователей");
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUser(Long id) {
        log.debug("Удаление пользователя с ID = {}", id);
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new NotFoundException(String.format(NOT_FOUND_MESSAGE, id));
        }
    }

    @Transactional(readOnly = true)
    private void isValidNotDuplicateEmail(String email) {
        if (userRepository.findByEmailContainingIgnoreCase(email).isPresent()) {
            throw new DuplicateEmailException(String.format("Пользователь с email = %s уже существует", email));
        }
    }
}
