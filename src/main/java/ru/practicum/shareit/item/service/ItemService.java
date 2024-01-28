package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item create(ItemDto itemDto, Long id);
    Item update(ItemDto item, Long id, Long userId);
    Item getItemById(Long id, Long userId);
    Collection<Item> getItemBySearch(String text);
    Collection<Item> getItemByUser(Long userId);

}
