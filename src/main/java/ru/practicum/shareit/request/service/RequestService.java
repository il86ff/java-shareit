package ru.practicum.shareit.request.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationItemException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemGetRequestDTO;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Getter
@Service
@RequiredArgsConstructor
@Slf4j
public class RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Transactional
    public ItemRequest create(ItemRequestDto itemRequestDto, Long id) {
        try {
            LocalDateTime time = LocalDateTime.now();
            ItemRequest itemRequest = ItemRequestMapper.dtoToItemRequest(itemRequestDto, userService.getById(id), time);
            return requestRepository.save(itemRequest);
        } catch (NullPointerException e) {
            throw new ValidationItemException("Запрос пуст");
        }
    }

    @Transactional(readOnly = true)
    public List<ItemGetRequestDTO> getListRequest(Long id) {
        User user = userService.getById(id);
        List<ItemGetRequestDTO> requestsResult = new ArrayList<>();

        requestRepository.findByRequester_IdOrderByCreatedDesc(id)
                .forEach(i -> requestsResult.add(ItemRequestMapper.transformationGetRequestDTO(
                        i, itemService.getByRequestId(i.getId()))));

        return requestsResult;
    }

    @Transactional(readOnly = true)
    public ItemGetRequestDTO getRequest(Long userId, Long requestId) {
        User user = userService.getById(userId);
        try {
            return ItemRequestMapper.transformationGetRequestDTO(requestRepository.findById(requestId).get(),
                    itemService.getByRequestId(requestId));
        } catch (NoSuchElementException e) {
            throw new NotFoundException(String.format("Запрос %d не существует", requestId));
        }
    }

    @Transactional(readOnly = true)
    public List<ItemGetRequestDTO> getListAllRequest(Long userId, Integer from, Integer size) {
        User user = userService.getById(userId);

        Pageable pageable = PageRequest.of(from / size, size,
                Sort.by(Sort.Direction.DESC, "created"));

        List<ItemGetRequestDTO> requestsResult = new ArrayList<>();
        if (from >= 0 && size > 0) {
            requestRepository.findAllByRequesterIdNot(userId, pageable)
                    .forEach(i -> requestsResult.add(ItemRequestMapper.transformationGetRequestDTO(
                            i, itemService.getByRequestId(i.getId()))));
            return requestsResult;
        } else {
            throw new ValidationItemException(String.format("Не верно указано количество предметов %d или страниц %d", from, size));
        }

    }

}
