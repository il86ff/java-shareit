package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ItemOwnerMismatchException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationItemException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDataDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

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

    private final User user = new User(
            1L,
            "name",
            "email@email"
    );

    private final ItemDto itemDto = new ItemDto(1L, "itemDto", "itemDtoDesc", true, null);
    private final Item item = new Item(
            1L,
            "перчатки",
            "резиновые",
            true,
            null,
            user
    );
    private final Comment comment = new Comment(1L, "test", item, user.getName(), LocalDateTime.now());

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
        User user1 = userService.create(userDto);
        itemService.create(itemDto, user1.getId());
        assertThrows(NotFoundException.class,
                () -> itemService.update(itemDto, 2L, user1.getId()));
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
    void getItemById_shouldReturnItemIfOwnerRequestingWithCommentAndBooking() {
        LocalDateTime time = LocalDateTime.now();
        User createdUser = userService.create(userDto);
        User createdBooker = userService.create(userDto2);
        Item createdItem = itemService.create(itemDto, createdUser.getId());
        BookingDto createdBookingDto = new BookingDto(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                createdItem.getId()
        );
        Booking createdBooking = bookingService.create(createdBookingDto, createdBooker.getId());
        createdBooking.setStart(createdBooking.getStart().minusDays(2));
        createdBooking.setEnd(createdBooking.getEnd().minusDays(2));
        bookingRepository.save(createdBooking);
        Comment createdComment = itemService.addComment(new CommentDto(null, "comment"), createdBooker.getId(), createdItem.getId());

        ItemDataDto itemDataDto = itemService.getItemById(createdItem.getId(), createdUser.getId());

        List<Booking> bookings = bookingRepository.findByItem_IdOrderByEndDesc(createdItem.getId()).stream().filter(i -> i.getStart().isBefore(time) && i.getItem().getUser().getId().equals(createdUser.getId())).collect(Collectors.toList());
        List<Booking> bookingNext = bookingRepository.findByItem_IdOrderByEndDesc(createdItem.getId()).stream().filter(p -> p.getEnd().isAfter(time) && p.getStart().isAfter(time) && p.getItem().getUser().getId().equals(createdUser.getId())).collect(Collectors.toList());

        assertFalse(bookings.isEmpty());
        assertTrue(bookingNext.isEmpty());
        assertEquals(createdComment.getAuthorName(), createdBooker.getName());
        assertEquals(createdComment.getItem().getId(), createdItem.getId());
        assertEquals(createdItem.getId(), itemDataDto.getId());
        assertEquals(createdItem.getUser().getEmail(), itemDataDto.getUser().getEmail());
    }

    @Test
    void getItemByUser_shouldReturnByUserId() {
        User thisUser = userService.create(userDto);
        itemService.create(itemDto, thisUser.getId());
        Collection<ItemDataDto> items = itemService.getItemByUser(thisUser.getId());

        assertFalse(items.isEmpty());
    }

    @Test
    void getItemByUser_shouldReturnByUserIdWithBookings() {
        LocalDateTime time = LocalDateTime.now();
        User createdUser = userService.create(userDto);
        User createdBooker = userService.create(userDto2);
        Item createdItem = itemService.create(itemDto, createdUser.getId());
        BookingDto createdBookingDto = new BookingDto(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                createdItem.getId()
        );
        Booking createdBooking = bookingService.create(createdBookingDto, createdBooker.getId());
        createdBooking.setStart(createdBooking.getStart().minusDays(2));
        createdBooking.setEnd(createdBooking.getEnd().minusDays(2));
        bookingRepository.save(createdBooking);
        Comment createdComment = itemService.addComment(new CommentDto(null, "comment"), createdBooker.getId(), createdItem.getId());

        Collection<ItemDataDto> itemDataDto = itemService.getItemByUser(createdUser.getId());

        BookingDto createdBookingDtoNext = new BookingDto(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                createdItem.getId()
        );

        Booking createdBookingNext = bookingService.create(createdBookingDtoNext, createdBooker.getId());

        List<Booking> bookings = bookingRepository.findByItem_IdOrderByEndDesc(createdItem.getId()).stream().filter(i -> i.getStart().isBefore(time) && i.getItem().getUser().getId().equals(createdUser.getId())).collect(Collectors.toList());
        List<Booking> bookingNext = bookingRepository.findByItem_IdOrderByEndDesc(createdItem.getId()).stream().filter(p -> p.getEnd().isAfter(time) && p.getStart().isAfter(time) && p.getItem().getUser().getId().equals(createdUser.getId())).collect(Collectors.toList());

        assertFalse(bookings.isEmpty());
        assertFalse(bookingNext.isEmpty());
        assertEquals(createdComment.getAuthorName(), createdBooker.getName());
        assertEquals(createdComment.getItem().getId(), createdItem.getId());
        assertFalse(itemDataDto.isEmpty());
    }

    @Test
    void search_shouldReturnItem() {
        User thisUser = userService.create(userDto);
        itemService.create(itemDto, thisUser.getId());
        Collection<Item> items = itemService.getItemBySearch(itemDto.getName());

        assertFalse(items.isEmpty());
    }

    @Test
    void search_shouldReturnEmptyList() {
        Collection<Item> items = itemService.getItemBySearch("");
        assertTrue(items.isEmpty());
    }

    @Test
    void validationItem_shouldThrowValidationItemExceptionName() {
        assertThrows(ValidationItemException.class,
                () -> itemService.validationItem(new ItemDto(null, null, "desc", true, null)));
    }

    @Test
    void validationItem_shouldThrowValidationItemExceptionDescription() {
        assertThrows(ValidationItemException.class,
                () -> itemService.validationItem(new ItemDto(null, "Name", "", true, null)));
    }

    @Test
    void validationItem_shouldThrowValidationItemExceptionAvailable() {
        assertThrows(ValidationItemException.class,
                () -> itemService.validationItem(new ItemDto(null, "Name", "Desc", null, null)));
    }

    @Test
    void comment_shouldAddComment() {
        User createdUser = userService.create(userDto);
        User createdBooker = userService.create(userDto2);
        Item createdItem = itemService.create(itemDto, createdUser.getId());
        BookingDto createdBookingDto = new BookingDto(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                createdItem.getId()
        );
        Booking createdBooking = bookingService.create(createdBookingDto, createdBooker.getId());
        createdBooking.setStart(createdBooking.getStart().minusDays(2));
        createdBooking.setEnd(createdBooking.getEnd().minusDays(2));
        bookingRepository.save(createdBooking);
        Comment createdComment = itemService.addComment(new CommentDto(null, "comment"), createdBooker.getId(), createdItem.getId());

        assertEquals(createdComment.getAuthorName(), createdBooker.getName());
        assertEquals(createdComment.getItem().getId(), createdItem.getId());
    }

    @Test
    void comment_shouldThrowException() {
        User createdUser = userService.create(userDto);
        User createdBooker = userService.create(userDto2);
        Item createdItem = itemService.create(itemDto, createdUser.getId());
        BookingDto createdBookingDto = new BookingDto(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                createdItem.getId()
        );
        Booking createdBooking = bookingService.create(createdBookingDto, createdBooker.getId());

        assertThrows(ValidationItemException.class,
                () -> itemService.addComment(new CommentDto(null, "comment"), createdBooker.getId(), createdItem.getId()));


    }
}
