package ru.practicum.shareit.item.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDataDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/items")
public class ItemController {
    private static final String HEADER_X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public Item create(@Valid @RequestBody ItemDto itemDto,
                       @RequestHeader(HEADER_X_SHARER_USER_ID) Long id) {
        return itemService.create(itemDto, id);
    }

    @PatchMapping("{id}")
    public Item update(@Valid @RequestBody ItemDto item,
                       @PathVariable Long id,
                       @RequestHeader(HEADER_X_SHARER_USER_ID) Long userId) {
        return itemService.update(item, id, userId);
    }

    @GetMapping("{id}")
    public ItemDataDto getItemById(@Valid @PathVariable Long id, @RequestHeader(HEADER_X_SHARER_USER_ID) Long userId) {
        return itemService.getItemById(id, userId);
    }

    @GetMapping
    public Collection<ItemDataDto> getItemById(@RequestHeader(HEADER_X_SHARER_USER_ID) Long userId) {
        return itemService.getItemByUser(userId);
    }

    @GetMapping("/search")
    public Collection<Item> getItemBySearch(@RequestParam String text, @RequestHeader(HEADER_X_SHARER_USER_ID) Long userId) {
        return itemService.getItemBySearch(text);
    }

    @PostMapping("/{itemId}/comment")
    public Comment addComment(@Valid @RequestBody CommentDto commentDto,
                              @RequestHeader(HEADER_X_SHARER_USER_ID) Long id,
                              @PathVariable Long itemId) {
        return itemService.addComment(commentDto, id, itemId);
    }

}
