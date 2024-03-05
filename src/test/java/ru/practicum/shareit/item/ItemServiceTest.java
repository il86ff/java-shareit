package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemOwnerMismatchException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDataDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final ItemService itemService;
    private final UserService userService;

    private final UserDto userDto = new UserDto(
            1L,
            "name",
            "email@email"
    );

    private final UserDto userDto2 = new UserDto(
            2L,
            "name2",
            "email2@email"
    );

    private final ItemDto itemDto = new ItemDto(1L, "itemDto", "itemDtoDesc", true, null);

    @Test
    void create_shouldThrowExceptionIfUserIdIsIncorrect() {
        assertThrows(NotFoundException.class,
                () -> itemService.create(itemDto, 999L));
    }

    @Test
    void update_shouldThrowExceptionIfUserIdIsIncorrect() {
        assertThrows(NotFoundException.class,
                () -> itemService.update(itemDto, 1L, 1L));
    }

    @Test
    void update_shouldThrowExceptionIfOwnerIdIsIncorrect() {
        userService.create(userDto);
        userService.create(userDto2);
        itemService.create(itemDto, 1L);
        assertThrows(ItemOwnerMismatchException.class,
                () -> itemService.update(itemDto, 1L, 2L));
    }

    @Test
    void update_shouldThrowExceptionIfItemIdIsIncorrect() {
        userService.create(userDto);
        itemService.create(itemDto, 1L);
        assertThrows(NotFoundException.class,
                () -> itemService.update(itemDto, 2L, 2L));
    }

    @Test
    void update_shouldUpdateIfItemNameIsNull() {
        User thisUser = userService.create(userDto);
        Item thisItem = itemService.create(itemDto, thisUser.getId());
        thisItem.setName(null);
        Item updatedItem = itemService.update(new ItemDto(thisItem.getId(),
                        thisItem.getName(), thisItem.getDescription(), thisItem.getAvailable(), thisItem.getRequestId()),
                thisUser.getId(), thisItem.getId());

        assertEquals(thisItem.getDescription(), updatedItem.getDescription());
        assertEquals(thisItem.getAvailable(), updatedItem.getAvailable());
    }

    @Test
    void update_shouldUpdateIfItemDescriptionIsNull() {
        User thisUser = userService.create(userDto);
        Item thisItem = itemService.create(itemDto, thisUser.getId());
        thisItem.setDescription(null);
        Item updatedItem = itemService.update(new ItemDto(thisItem.getId(),
                        thisItem.getName(), thisItem.getDescription(), thisItem.getAvailable(), thisItem.getRequestId()),
                thisUser.getId(), thisItem.getId());

        assertEquals(thisItem.getName(), updatedItem.getName());
        assertEquals(thisItem.getAvailable(), updatedItem.getAvailable());
    }

    @Test
    void update_shouldUpdateIfItemAvailableIsNull() {
        User thisUser = userService.create(userDto);
        Item thisItem = itemService.create(itemDto, thisUser.getId());
        thisItem.setAvailable(null);
        Item updatedItem = itemService.update(new ItemDto(thisItem.getId(),
                        thisItem.getName(), thisItem.getDescription(), thisItem.getAvailable(), thisItem.getRequestId()),
                thisUser.getId(), thisItem.getId());

        assertEquals(thisItem.getName(), updatedItem.getName());
        assertEquals(thisItem.getDescription(), updatedItem.getDescription());
    }

    @Test
    void getItemById_shouldThrowExceptionIfIdIsIncorrect() {
        assertThrows(NotFoundException.class,
                () -> itemService.getItemById(999L, 999L));
    }

    @Test
    void getItemById_shouldReturnItemIfOwnerRequesting() {
        User thisUser = userService.create(userDto);
        Item thisItem = itemService.create(itemDto, thisUser.getId());
        ItemDataDto returnedItem = itemService.getItemById(thisUser.getId(), thisItem.getId());

        assertEquals(returnedItem.getUser().getEmail(), thisUser.getEmail());
        assertEquals(thisItem.getName(), returnedItem.getName());
    }

    @Test
    void getItemByUser_shouldReturnByUserId() {
        User thisUser = userService.create(userDto);
        itemService.create(itemDto, thisUser.getId());
        Collection<ItemDataDto> items = itemService.getItemByUser(thisUser.getId());

        assertFalse(items.isEmpty());
    }

    @Test
    void search_shouldReturnItem() {
        User thisUser = userService.create(userDto);
        itemService.create(itemDto, thisUser.getId());
        Collection<Item> items = itemService.getItemBySearch(itemDto.getName());

        assertFalse(items.isEmpty());
    }
}
