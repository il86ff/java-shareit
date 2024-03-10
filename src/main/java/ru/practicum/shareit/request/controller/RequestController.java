package ru.practicum.shareit.request.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemGetRequestDTO;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public ItemRequest create(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemRequestDto request) {
        return requestService.create(request, userId);
    }

    @GetMapping
    public List<ItemGetRequestDTO> getByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getListRequest(userId);
    }

    @GetMapping("/all")
    public List<ItemGetRequestDTO> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @RequestParam(defaultValue = "0") Integer from,
                                   @RequestParam(required = false, defaultValue = "10") Integer size) {
        return requestService.getListAllRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemGetRequestDTO getById(@PathVariable("requestId") Long requestId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getRequest(userId, requestId);
    }
}
