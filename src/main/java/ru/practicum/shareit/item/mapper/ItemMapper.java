package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {
    public static Item toDtoItem(ItemDto itemDto, Long userId) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), userId);
    }

    public static Item dtoItemUpdate(ItemDto itemDto, Item item, Long userId) {
        return new Item(itemDto.getId() != null ? itemDto.getId() : item.getId(), itemDto.getName() != null ? itemDto.getName() : item.getName(), itemDto.getDescription() != null ? itemDto.getDescription() : item.getDescription(), itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getAvailable(), userId);
    }
}
