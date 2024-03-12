package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.ItemDataDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

@UtilityClass
public class ItemMapper {
    public static Item toDtoItem(ItemDto itemDto, User user) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), itemDto.getRequestId(), user);
    }

    public static Item dtoItemUpdate(ItemDto itemDto, Item item, User user) {
        return new Item(itemDto.getId() != null ? itemDto.getId() : item.getId(), itemDto.getName() != null ? itemDto.getName() : item.getName(), itemDto.getDescription() != null ? itemDto.getDescription() : item.getDescription(), itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getAvailable(), item.getRequestId(), user);
    }

    public static ItemDataDto itemToDataDtoNoBooking(Item item, List<Comment> comment) {
        return new ItemDataDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), null, null, comment, item.getUser(), item.getRequestId());
    }

    public static ItemDataDto itemToDataDto(List<Booking> booking, List<Booking> bookingNext, List<Comment> comment) {
        return new ItemDataDto(booking.get(0).getItem().getId(), booking.get(0).getItem().getName(), booking.get(0).getItem().getDescription(), booking.get(0).getItem().getAvailable(), BookingMapper.bookingDtoNoItem(booking.get(0)), bookingNext.size() > 1 ? BookingMapper.bookingDtoNoItem(bookingNext.get(bookingNext.size() - 1)) : null, comment, booking.get(0).getItem().getUser(), booking.get(0).getItem().getRequestId());
    }
}
