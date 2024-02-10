package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemOwnerMismatchException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationItemException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private static final String NOT_FOUND_MESSAGE = "предмет с id = %s не найден...";
    private final ItemRepository itemRepository;
    private final UserService userService;

    public ItemServiceImpl(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public Item create(ItemDto itemDto, Long id) {
        log.debug("Попытка создания предмета {}", itemDto);
        userService.getById(id);
        validationItem(itemDto);
        return itemRepository.create(itemDto, id);
    }

    @Override
    public Item update(ItemDto item, Long id, Long userId) {
        log.debug("Попытка обновления предмета {}", item);
        userService.getById(userId);
        Item oldItem = getItemById(id, userId);
        if (oldItem.getUserId().compareTo(userId) != 0)
            throw new ItemOwnerMismatchException("предмет пренадлежит другому пользователю...");
        Item itemUpdate = ItemMapper.dtoItemUpdate(item, oldItem, userId);
        itemUpdate.setId(id);
        return itemRepository.update(itemUpdate, id, userId);

    }

    @Override
    public Item getItemById(Long id, Long userId) {
        log.debug("Получение дынных о предмете с ID = {}", id);
        if (itemRepository.getItemById(id).isPresent()) {
            return itemRepository.getItemById(id).get();
        } else {
            throw new NotFoundException(String.format(NOT_FOUND_MESSAGE, id));
        }
    }

    @Override
    public Collection<Item> getItemBySearch(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        log.debug("Получение дынных о предмете по маске %{}%", text);
        return itemRepository.getAllItems().stream().filter(item -> item.getDescription().toUpperCase().contains(text.toUpperCase()) || item.getName().toUpperCase().contains(text.toUpperCase())).filter(Item::getAvailable).collect(Collectors.toList());
    }

    @Override
    public Collection<Item> getItemByUser(Long userId) {
        log.debug("Получение дынных о предметах с владельцем ID = {}", userId);
        userService.getById(userId);
        return itemRepository.getAllItems().stream().filter(item -> item.getUserId().compareTo(userId) == 0).filter(Item::getAvailable).collect(Collectors.toList());
    }

    private void validationItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationItemException(String.format("У предмета %s нет названия", itemDto));
        }

        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationItemException(String.format("У предмета %s нет описания", itemDto));
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationItemException(String.format("У предмета %s нет статуса доступности", itemDto));
        }
    }

}
