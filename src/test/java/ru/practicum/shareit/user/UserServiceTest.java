package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.EmptyEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private final UserService userService;

    @Test
    void shouldThrowExceptionIfEmailIsNull() {
        assertThrows(EmptyEmailException.class,
                () -> userService.create(new UserDto(999L, "Someone", null)));
    }

    @Test
    void shouldThrowExceptionIfEmailIsDuplicate() {
        userService.create(new UserDto(999L, "Someone", "email@mail.ru"));
        assertThrows(DuplicateEmailException.class,
                () -> userService.create(new UserDto(null, "Someone1", "email@mail.ru")));
    }


    @Test
    void shouldThrowExceptionIfIdIsInvalid() {
        assertThrows(NotFoundException.class,
                () -> userService.getById(1L));
    }

    @Test
    void shouldCorrectlyGetById() {
        UserDto userDto = new UserDto(null, "Someone", "email@email");
        User createdUser = userService.create(userDto);

        assertEquals(createdUser.getEmail(), userService.getById(createdUser.getId()).getEmail());
        assertEquals(createdUser.getName(), userService.getById(createdUser.getId()).getName());
    }

    @Test
    void shouldCorrectlyCreateUser() {
        UserDto userDto = new UserDto(999L, "Someone", "email@email");
        User newUser = UserMapper.dtoToUser(userDto);
        User createdUser = userService.create(userDto);

        assertEquals(createdUser.getEmail(), newUser.getEmail());
        assertEquals(createdUser.getName(), newUser.getName());
    }

    @Test
    void shouldCorrectlyUpdateUser() {
        UserDto userDto = new UserDto(999L, "Someone", "email@email");
        User thisUser = userService.create(userDto);
        thisUser.setName("NewName");
        User updatedUser = userService.update(userDto, thisUser.getId());

        assertEquals(updatedUser.getEmail(), thisUser.getEmail());
        assertEquals(updatedUser.getName(), thisUser.getName());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsEquals() {
        UserDto userDto = new UserDto(999L, "Someone", "email@email");
        UserDto userDtoAnother = new UserDto(999L, "Someone", "emailNew@email");
        userService.create(userDtoAnother);
        User user = userService.create(userDto);
        userDto.setEmail("emailNew@email");

        assertThrows(DuplicateEmailException.class,
                () -> userService.update(userDto, user.getId()));
    }

    @Test
    void shouldDeleteById() {
        UserDto userDto = new UserDto(null, "Someone", "email@email");
        User thisUser = userService.create(userDto);
        userService.deleteUser(thisUser.getId());

        assertTrue(userService.getAllUsers().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenDeleteWrongUser() {
        UserDto userDto = new UserDto(999L, "Someone", "email@email");
        User user = userService.create(userDto);

        assertThrows(NotFoundException.class,
                () -> userService.deleteUser(user.getId() + 1));
    }

    @Test
    void shouldReturnListOfUsers() {
        User first = userService.create(new UserDto(1L, "user1", "user1@mail.ru"));
        User second = userService.create(new UserDto(2L, "user2", "user2@mail.ru"));

        assertEquals(2, userService.getAllUsers().size());
        assertTrue(userService.getAllUsers().contains(first));
        assertTrue(userService.getAllUsers().contains(second));
    }
}
