package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.entity.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByUserId(long userId);

    List<Item> findByNameOrDescriptionContainingIgnoreCase(String name, String description);

}
