package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationItemException;
import ru.practicum.shareit.request.dto.ItemGetRequestDTO;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceTest {
    private final RequestService requestService;
    private final UserService userService;

    private final User user = new User(1L, "user", "email@email.ru");
    private final UserDto userDto = new UserDto(999L, "Someone", "email@email.ru");
    private final UserDto userDto2 = new UserDto(999L, "Someone2", "email2@email.ru");
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "request", userDto, LocalDateTime.of(2024, 2, 2, 2, 2));
    private final ItemGetRequestDTO itemGetRequestDto = new ItemGetRequestDTO(1L, "request", LocalDateTime.of(2024, 2, 2, 2, 2), List.of());

    @Test
    void create_shouldCreateRequest() {
        User thisUser = userService.create(userDto);
        ItemRequest thisRequest = requestService.create(itemRequestDto, thisUser.getId());

        assertThat(thisRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
    }

    @Test
    void create_shouldThrowExceptionIfUserIdIsIncorrect() {
        assertThrows(NotFoundException.class,
                () -> requestService.create(itemRequestDto, 999L));
    }

    @Test
    void create_shouldThrowExceptionIfRequestIsNull() {
        userService.create(userDto);
        assertThrows(ValidationItemException.class,
                () -> requestService.create(null, 1L));
    }

    @Test
    void getRequestsByOwner_shouldReturnRequests() {
        User thisUser = userService.create(userDto);
        ItemRequest thisRequest = requestService.create(itemRequestDto, thisUser.getId());
        List<ItemGetRequestDTO> returnedRequest = requestService.getListRequest(thisUser.getId());

        assertFalse(returnedRequest.isEmpty());
        assertTrue(returnedRequest.contains(ItemRequestMapper.transformationGetRequestDTO(thisRequest, List.of())));
    }

    @Test
    void getListRequest_shouldThrowExceptionIfUserIdIncorrect() {
        assertThrows(NotFoundException.class,
                () -> requestService.getListRequest(999L));
    }

    @Test
    void getRequest_shouldThrowExceptionIfUserIdIncorrect() {
        assertThrows(NotFoundException.class,
                () -> requestService.getRequest(1L, 999L));
    }

    @Test
    void getRequest_shouldReturnRequests() {
        User thisUser = userService.create(userDto);
        ItemRequest thisRequest = requestService.create(itemRequestDto, thisUser.getId());
        ItemGetRequestDTO returnedRequest = requestService.getRequest(thisUser.getId(), thisRequest.getId());

        assertEquals(thisRequest.getDescription(), returnedRequest.getDescription());
        assertEquals(thisRequest.getId(), returnedRequest.getId());
    }

    @Test
    void getRequest_shouldThrowExceptionIfRequestIdIncorrect() {
        userService.create(userDto);
        assertThrows(NotFoundException.class,
                () -> requestService.getRequest(1L, 999L));
    }

    @Test
    void getListAllRequest_shouldThrowExceptionIfUserIdIncorrect() {
        assertThrows(NotFoundException.class,
                () -> requestService.getListAllRequest(1L, 0, 10));
    }

    @Test
    void getListAllRequest_shouldThrowExceptionIfFromIsIncorrect() {
        userService.create(userDto);
        assertThrows(ValidationItemException.class,
                () -> requestService.getListAllRequest(1L, -2, 10));
    }

    @Test
    void getListAllRequest_shouldReturnRequests() {
        User thisUser = userService.create(userDto);
        User anotherUser = userService.create(userDto2);
        ItemRequest thisRequest = requestService.create(itemRequestDto, thisUser.getId());
        List<ItemGetRequestDTO> returnedRequest = requestService.getListAllRequest(anotherUser.getId(), 1, 10);

        assertFalse(returnedRequest.isEmpty());
        assertTrue(returnedRequest.contains(ItemRequestMapper.transformationGetRequestDTO(thisRequest, List.of())));
    }

}
