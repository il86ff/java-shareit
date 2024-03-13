package ru.practicum.shareit.gateway.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.item.dto.CommentDto;
import ru.practicum.shareit.gateway.item.dto.ItemDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private static final String HEADER_X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemClient itemClient;


    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto,
                       @RequestHeader(HEADER_X_SHARER_USER_ID) Long id) {
        return itemClient.create(itemDto, id);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> update(@Valid @RequestBody ItemDto item,
                       @PathVariable Long id,
                       @RequestHeader(HEADER_X_SHARER_USER_ID) Long userId) {
        return itemClient.update(item, id, userId);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getItemById(@Valid @PathVariable Long id, @RequestHeader(HEADER_X_SHARER_USER_ID) Long userId) {
        return itemClient.getItemById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemById(@RequestHeader(HEADER_X_SHARER_USER_ID) Long userId) {
        return itemClient.getItemByUser(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemBySearch(@RequestParam String text, @RequestHeader(HEADER_X_SHARER_USER_ID) Long userId) {
        return itemClient.getItemBySearch(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Valid @RequestBody CommentDto commentDto,
                              @RequestHeader(HEADER_X_SHARER_USER_ID) Long id,
                              @PathVariable Long itemId) {
        return itemClient.addComment(commentDto, id, itemId);
    }

}
