package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDataDto {
    protected Long id;
    protected LocalDateTime start;
    protected LocalDateTime end;
    protected Long bookerId;
}