package ru.practicum.shareit.item.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;

import java.util.Collection;
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public Item create(@Valid @RequestBody ItemDto itemDto,
                       @RequestHeader("X-Sharer-User-Id") Long id) {
        return itemService.create(itemDto, id);
    }

    @PatchMapping("{id}")
    public Item update(@Valid @RequestBody ItemDto item,
                       @PathVariable Long id,
                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.update(item, id, userId);
    }

    @GetMapping("{id}")
    public Item getItemById(@Valid @PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemById(id, userId);
    }

    @GetMapping
    public Collection<Item> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemByUser(userId);
    }

    @GetMapping("/search")
    public Collection<Item> getItemBySearch(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemBySearch(text);
    }

}
