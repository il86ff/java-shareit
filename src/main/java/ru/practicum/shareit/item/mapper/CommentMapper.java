package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class CommentMapper {
    public static Comment commentDtoToComment(CommentDto commentDto, List<Booking> booking, LocalDateTime time) {
        return new Comment(commentDto.getId(), commentDto.getText(), booking.get(0).getItem(), booking.get(0).getBooker().getName(), time);
    }
}
