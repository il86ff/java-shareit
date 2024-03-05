package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.dto.ItemGetRequestDTO;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.entity.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
public class RequestControllerTest {
    private final User user = new User(1L, "user", "email@email.ru");
    private final ItemRequest itemRequest = new ItemRequest(1L, "request", user, LocalDateTime.of(2024, 2, 2, 2, 2));
    private final ItemGetRequestDTO itemGetRequestDto = new ItemGetRequestDTO(1L, "request", LocalDateTime.of(2024, 2, 2, 2, 2), List.of());
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    RequestService requestService;
    @Autowired
    private MockMvc mvc;

    @SneakyThrows
    @Test
    void create_shouldCreateRequest() {
        when(requestService.create(any(), any(Long.class)))
                .thenReturn(itemRequest);

        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.requester.id", is(itemRequest.getRequester().getId()), Long.class))
                .andExpect(jsonPath("$.requester.name", is(itemRequest.getRequester().getName())))
                .andExpect(jsonPath("$.requester.email", is(itemRequest.getRequester().getEmail())))
                .andExpect(jsonPath("$.created",
                        is(itemRequest.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @SneakyThrows
    @Test
    void getRequest_shouldReturnListOfRequests() {
        when(requestService.getRequest(any(Long.class), any(Long.class)))
                .thenReturn(itemGetRequestDto);

        mvc.perform(get("/requests/1")
                        .content(objectMapper.writeValueAsString(itemGetRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemGetRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemGetRequestDto.getDescription())))
                .andExpect(jsonPath("$.created",
                        is(itemGetRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @SneakyThrows
    @Test
    void getListAllRequest_shouldReturnListOfRequests() {
        when(requestService.getListAllRequest(any(Long.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(itemGetRequestDto));

        mvc.perform(get("/requests/all")
                        .content(objectMapper.writeValueAsString(itemGetRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemGetRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemGetRequestDto.getDescription())))
                .andExpect(jsonPath("$.[0].created",
                        is(itemGetRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @SneakyThrows
    @Test
    void getListRequest_shouldReturnRequestsByOwner() {
        when(requestService.getListRequest(any(Long.class)))
                .thenReturn(List.of(itemGetRequestDto));

        mvc.perform(get("/requests")
                        .content(objectMapper.writeValueAsString(itemGetRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemGetRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemGetRequestDto.getDescription())))
                .andExpect(jsonPath("$.[0].created",
                        is(itemGetRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }
}
