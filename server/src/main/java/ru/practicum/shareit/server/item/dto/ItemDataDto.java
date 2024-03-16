package ru.practicum.shareit.server.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.booking.dto.BookingDataDto;
import ru.practicum.shareit.server.item.entity.Comment;
import ru.practicum.shareit.server.user.entity.User;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDataDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDataDto lastBooking;
    private BookingDataDto nextBooking;
    private List<Comment> comments;
    private User user;
    private Long requestId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDataDto that = (ItemDataDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
