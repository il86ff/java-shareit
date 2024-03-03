package ru.practicum.shareit.booking.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String HEADER_X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public Booking create(@Valid @RequestBody BookingDto booking, @RequestHeader(HEADER_X_SHARER_USER_ID) Long id) {
        return bookingService.create(booking, id);
    }

    @PatchMapping("{bookingId}")
    public Booking update(@Valid @PathVariable Long bookingId,
                          @RequestHeader(HEADER_X_SHARER_USER_ID) Long userId,
                          @RequestParam Boolean approved) {

        return bookingService.update(bookingId, userId, approved);
    }

    @GetMapping
    public Collection<Booking> getAllBookingByUser(@Valid @RequestHeader("X-Sharer-User-Id") Long id,
                                                   @RequestParam(defaultValue = "ALL") BookingStatus state,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.getAllBookingByUser(id, state, from, size);
    }

    @GetMapping("{bookingId}")
    public Booking getBookingByUser(@Valid @PathVariable Long bookingId,
                                    @RequestHeader(HEADER_X_SHARER_USER_ID) Long userId) {

        return bookingService.getBookingByUser(bookingId, userId);
    }

    @GetMapping("/owner") //Если есть вещь, нужно найти брони и выдать
    public Collection<Booking> getAllBookingItemByUser(@Valid @RequestHeader("X-Sharer-User-Id") Long id,
                                                       @RequestParam(defaultValue = "ALL") BookingStatus state,
                                                       @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.getAllBookingItemByUser(id, state, from, size);
    }
}
