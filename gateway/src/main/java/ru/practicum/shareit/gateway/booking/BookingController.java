package ru.practicum.shareit.gateway.booking;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.booking.dto.BookingDto;
import ru.practicum.shareit.gateway.booking.dto.BookingStatus;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private static final String HEADER_X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody BookingDto booking, @RequestHeader(HEADER_X_SHARER_USER_ID) Long id) {
        return bookingClient.create(id, booking);
    }

    @PatchMapping("{bookingId}")
    public  ResponseEntity<Object> update(@Valid @PathVariable Long bookingId,
                          @RequestHeader(HEADER_X_SHARER_USER_ID) Long userId,
                          @RequestParam Boolean approved) {

        return bookingClient.update(userId, bookingId, approved);
    }

    @GetMapping
    public  ResponseEntity<Object> getAllBookingByUser(@Valid @RequestHeader("X-Sharer-User-Id") Long id,
                                                   @RequestParam(defaultValue = "ALL") BookingStatus state,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(defaultValue = "10") Integer size) {
        return bookingClient.getAllBookingByUser(id, state, from, size);
    }

    @GetMapping("{bookingId}")
    public  ResponseEntity<Object> getBookingByUser(@Valid @PathVariable Long bookingId,
                                    @RequestHeader(HEADER_X_SHARER_USER_ID) Long userId) {

        return bookingClient.getBookingByUser(userId, bookingId);
    }

    @GetMapping("/owner")
    public  ResponseEntity<Object> getAllBookingItemByUser(@Valid @RequestHeader("X-Sharer-User-Id") Long id,
                                                       @RequestParam(defaultValue = "ALL") BookingStatus state,
                                                       @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(defaultValue = "10") Integer size) {
        return bookingClient.getAllBookingItemByUser(id, state, from, size);
    }
}