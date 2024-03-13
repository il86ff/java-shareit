package ru.practicum.shareit.server.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.server.booking.dto.BookingDataDto;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingStatus;
import ru.practicum.shareit.server.booking.entity.Booking;
import ru.practicum.shareit.server.item.entity.Item;
import ru.practicum.shareit.server.user.entity.User;

import java.time.LocalDateTime;

@UtilityClass
public class BookingMapper {
    public static Booking toDtoNewBooking(BookingDto bookingDto, User user, Item item, LocalDateTime time) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart() == null ? time : bookingDto.getStart(),
                bookingDto.getEnd() == null ? time : bookingDto.getEnd(),
                item,
                user,
                BookingStatus.WAITING);
    }

    public static BookingDataDto bookingDtoNoItem(Booking booking) {
        return new BookingDataDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker().getId()
        );
    }
}
