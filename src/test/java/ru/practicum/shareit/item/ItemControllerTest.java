package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDataDto;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.entity.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    private final User testUser = new User(
            1L,
            "test",
            "test@mail.ru");
    private final Item testItem = new Item(1L, "item", "item for job", true, null, testUser);
    private final Comment comment = new Comment(1L, "comment", testItem, testUser.getName(), LocalDateTime.of(2021, 2, 2, 2, 2));
    private final ItemDataDto testItemDataDto = new ItemDataDto(1L, "test", "item for job", true, null, null, null, testUser, null);
    
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemService itemService;
    @Autowired
    private MockMvc mvc;

    @SneakyThrows
    @Test
    void shouldCorrectlyCreateItem() {
        when(itemService.create(any(), any(Long.class)))
                .thenReturn(testItem);

        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(testItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testItem.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(testItem.getName())))
                .andExpect(jsonPath("$.description", is(testItem.getDescription())))
                .andExpect(jsonPath("$.available", is(testItem.getAvailable())));
    }

    @SneakyThrows
    @Test
    void shouldReturnItemById() {
        when(itemService.getItemById(any(Long.class), any(Long.class)))
                .thenReturn(testItemDataDto);

        mvc.perform(get("/items/1")
                        .content(objectMapper.writeValueAsString(testItemDataDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testItemDataDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(testItemDataDto.getName())))
                .andExpect(jsonPath("$.description", is(testItemDataDto.getDescription())))
                .andExpect(jsonPath("$.available", is(testItemDataDto.getAvailable())));
    }

    @SneakyThrows
    @Test
    void shouldReturnListOfItems() {
        when(itemService.getItemByUser(any(Long.class)))
                .thenReturn(List.of(testItemDataDto));

        mvc.perform(get("/items")
                        .content(objectMapper.writeValueAsString(testItemDataDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(testItemDataDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(testItemDataDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(testItemDataDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(testItemDataDto.getAvailable())));
    }

    @SneakyThrows
    @Test
    void shouldUpdateItem() {
        when(itemService.update(any(), any(Long.class), any(Long.class)))
                .thenReturn(testItem);

        mvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(testItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testItem.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(testItem.getName())))
                .andExpect(jsonPath("$.description", is(testItem.getDescription())))
                .andExpect(jsonPath("$.available", is(testItem.getAvailable())));
    }

    @SneakyThrows
    @Test
    void shouldReturnItemsList() {
        when(itemService.getItemBySearch(any(String.class)))
                .thenReturn(List.of(testItem));

        mvc.perform(get("/items/search?text=description")
                        .content(objectMapper.writeValueAsString(testItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(testItem.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(testItem.getName())))
                .andExpect(jsonPath("$.[0].description", is(testItem.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(testItem.getAvailable())));
    }

    @SneakyThrows
    @Test
    void shouldCreateComment() {
        when(itemService.addComment(any(), any(Long.class), any(Long.class)))
                .thenReturn(comment);

        mvc.perform(post("/items/1/comment")
                        .content(objectMapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthorName())))
                .andExpect(jsonPath("$.created",
                        is(comment.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }
}
