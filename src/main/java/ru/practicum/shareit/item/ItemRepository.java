package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemRepository {
    private final Map<Long, Item> items;
    private Long id = 0L;

    public ItemRepository() {
        this.items = new HashMap<>();
    }

    public Item create(ItemDto item, Long userId) {
        Item newItem = ItemMapper.toDtoItem(item, userId);
        if (newItem.getId() == null) newItem.setId(++id);
        items.put(newItem.getId(), newItem);
        return newItem;
    }

    public Item update(Item item, Long id, Long userId) {
        items.put(id, item);
        return items.get(id);
    }

    public Optional<Item> getItemById(Long id) {
        return Optional.of(items.get(id));
    }

    public Collection<Item> getAllItems() {
        return items.values();
    }
}
