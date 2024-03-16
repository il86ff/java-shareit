package ru.practicum.shareit.server.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.server.item.entity.Item;
import ru.practicum.shareit.server.request.dto.ItemGetRequestDTO;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.entity.ItemRequest;
import ru.practicum.shareit.server.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {

    public static ItemRequest dtoToItemRequest(ItemRequestDto itemRequest, User user, LocalDateTime time) {
        return new ItemRequest(
                itemRequest.getId(),
                itemRequest.getDescription(),
                user,
                time
        );
    }

    public static ItemGetRequestDTO transformationGetRequestDTO(ItemRequest request, List<Item> item) {
        return new ItemGetRequestDTO(request.getId(),
                request.getDescription(),
                request.getCreated(),
                item);
    }
}
