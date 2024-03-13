package ru.practicum.shareit.server.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.server.item.entity.Item;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemGetRequestDTO {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<Item> items;
}
