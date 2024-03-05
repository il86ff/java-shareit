package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestService;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class RequestRepositoryTest {
    @Mock
    private RequestRepository requestRepository;
    private RequestService requestService;

    @Test
    void getById_shouldThrowExceptionIfWrongId() {
        requestService = new RequestService(requestRepository, null, null);

        assertThrows(NullPointerException.class,
                () -> requestService.getRequest(1L, 999L));
    }
}
