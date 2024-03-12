package ru.practicum.shareit.user;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryTest {
    private final UserDto user = new UserDto(1L, "name", "email@ya.ru");
    @Mock
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    void shouldReturnUserById() {
        User newUser = UserMapper.dtoToUser(user);
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(newUser));
        User thisUser = userService.getById(1L);
        verify(userRepository, Mockito.times(2)).findById(1L);

        assertThat(user.getName(), equalTo(thisUser.getName()));
        assertThat(user.getEmail(), equalTo(thisUser.getEmail()));
    }

    @Test
    void shouldThrowExceptionIfUserIdIsInvalid() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.getById(999L));
    }

    @Test
    void shouldThrowExceptionIfEmailExists() {
        when(userRepository.save(any()))
                .thenThrow(new DuplicateEmailException(""));
        assertThrows(DuplicateEmailException.class, () -> userService.create(user));
    }
}
