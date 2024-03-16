package ru.practicum.shareit.server.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.entity.Booking;
import ru.practicum.shareit.server.booking.repository.BookingRepository;
import ru.practicum.shareit.server.exception.ItemOwnerMismatchException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.exception.ValidationItemException;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDataDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.entity.Comment;
import ru.practicum.shareit.server.item.entity.Item;
import ru.practicum.shareit.server.item.mapper.CommentMapper;
import ru.practicum.shareit.server.item.mapper.ItemMapper;
import ru.practicum.shareit.server.item.repository.CommentRepository;
import ru.practicum.shareit.server.item.repository.ItemRepository;
import ru.practicum.shareit.server.user.entity.User;
import ru.practicum.shareit.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemService {
    private static final String NOT_FOUND_MESSAGE = "предмет с id = %s не найден...";
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemService(ItemRepository itemRepository, UserService userService, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public Item create(ItemDto itemDto, Long id) {
        log.debug("Попытка создания предмета {}", itemDto);
        validationItem(itemDto);
        userService.getById(id);
        Item item = ItemMapper.toDtoItem(itemDto, userService.getById(id));
        return itemRepository.save(item);
    }

    @Transactional
    public Item update(ItemDto item, Long id, Long userId) {
        log.debug("Попытка обновления предмета {}", item);
        User user = userService.getById(userId);
        if (itemRepository.findById(id).isPresent()) {
            Item itemDB = itemRepository.findById(id).get();
            if (itemDB.getUser().getId().compareTo(userId) != 0)
                throw new ItemOwnerMismatchException("предмет пренадлежит другому пользователю...");
            Item itemUpdate = ItemMapper.dtoItemUpdate(item, itemDB, user);
            itemUpdate.setId(id);
            return itemRepository.save(itemUpdate);
        } else {
            throw new NotFoundException(String.format(NOT_FOUND_MESSAGE, id));
        }
    }

    @Transactional(readOnly = true)
    public ItemDataDto getItemById(Long id, Long userID) {
        log.debug("Получение дынных о предмете с ID = {}", id);
        if (itemRepository.findById(id).isPresent()) {
            Item item = itemRepository.findById(id).get();
            List<Comment> comments = commentRepository.findByItem_Id(id);
            LocalDateTime time = LocalDateTime.now();

            List<Booking> bookings = bookingRepository.findByItem_IdOrderByEndDesc(id).stream().filter(i -> i.getStart().isBefore(time) && i.getItem().getUser().getId().equals(userID)).collect(Collectors.toList());

            List<Booking> bookingNext = bookingRepository.findByItem_IdOrderByEndDesc(id).stream().filter(p -> p.getEnd().isAfter(time) && p.getStart().isAfter(time) && p.getItem().getUser().getId().equals(userID)).collect(Collectors.toList());
            if (bookings.size() > 0) {
                return ItemMapper.itemToDataDto(bookings, bookingNext, comments);
            } else {
                return ItemMapper.itemToDataDtoNoBooking(item, comments);
            }
        } else {
            throw new NotFoundException(String.format(NOT_FOUND_MESSAGE, id));
        }
    }

    @Transactional(readOnly = true)
    public Collection<Item> getItemBySearch(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        log.debug("Получение дынных о предмете по маске %{}%", text);
        return itemRepository.findByNameOrDescriptionContainingIgnoreCase(text, text).stream().filter(Item::getAvailable).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Collection<ItemDataDto> getItemByUser(Long userId) {
        log.debug("Получение дынных о предметах с владельцем ID = {}", userId);
        userService.getById(userId);
        List<Item> items = itemRepository.findByUserId(userId);
        List<ItemDataDto> dataDtoList = new ArrayList<>();

        while (!items.isEmpty()) {
            dataDtoList.add(ItemMapper.itemToDataDtoNoBooking(items.get(0), commentRepository.findByItem_Id(items.get(0).getId())));
            items.remove(0);
        }

        List<ItemDataDto> bookingItems = new ArrayList<>();
        LocalDateTime time = LocalDateTime.now();

        List<Booking> booking = bookingRepository.findByItem_User_IdOrderByEndDesc(userId).stream().filter(p -> p.getEnd().isBefore(time) && p.getStart().isBefore(time) && p.getItem().getUser().getId().equals(userId)).collect(Collectors.toList());

        List<Booking> bookingNext = bookingRepository.findByItem_User_IdOrderByEndDesc(userId).stream().filter(p -> p.getEnd().isAfter(time) && p.getStart().isAfter(time) && p.getItem().getUser().getId().equals(userId)).collect(Collectors.toList());

        if (!booking.isEmpty() && !bookingNext.isEmpty()) {
            while (!booking.isEmpty() && !bookingNext.isEmpty()) {
                bookingItems.add(ItemMapper.itemToDataDto(booking, bookingNext, commentRepository.findByItem_Id(booking.get(0).getItem().getId())));
                booking.remove(0);
                bookingNext.remove(bookingNext.size() - 1);
            }
            bookingItems.addAll(dataDtoList);
            return bookingItems.stream().distinct().sorted(Comparator.comparing(ItemDataDto::getId)).collect(Collectors.toList());
        } else {
            log.debug("Запрошены предметы пользователя c id: {}", userId);
            return dataDtoList.stream().distinct().sorted(Comparator.comparing(ItemDataDto::getId)).collect(Collectors.toList());
        }
    }

    @Transactional
    public Comment addComment(CommentDto commentDto, Long id, Long itemId) {
        LocalDateTime time = LocalDateTime.now();
        List<Booking> booking = bookingRepository.findByBooker_IdAndItem_Id(id, itemId).stream().filter(b -> b.getEnd().isBefore(time)).collect(Collectors.toList());

        if (!booking.isEmpty() && !commentDto.getText().isEmpty()) {
            Comment comment = CommentMapper.commentDtoToComment(commentDto, booking, time);
            return commentRepository.save(comment);
        } else {
            throw new ValidationItemException(String.format("Предмет с id %d не был арендован или не существует", itemId));
        }
    }

    @Transactional(readOnly = true)
    public List<Item> getByRequestId(Long requestId) {
        return itemRepository.findByRequestId(requestId);
    }

    public void validationItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationItemException(String.format("У предмета %s нет названия", itemDto));
        }

        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationItemException(String.format("У предмета %s нет описания", itemDto));
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationItemException(String.format("У предмета %s нет статуса доступности", itemDto));
        }
    }

}
