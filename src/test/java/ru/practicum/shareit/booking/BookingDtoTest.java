package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    private final BookingDto booking = new BookingDto(
            1L,
            LocalDateTime.of(2020, 10, 10, 10, 10, 0),
            LocalDateTime.of(2020, 12, 10, 10, 10, 0),
            1L
    );
    @Autowired
    private JacksonTester<BookingDto> json;

    @SneakyThrows
    @Test
    void testBookingDto() {
        JsonContent<BookingDto> result = json.write(booking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2020-10-10T10:10:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2020-12-10T10:10:00");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
    }
}
