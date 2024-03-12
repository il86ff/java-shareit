package ru.practicum.shareit.item;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;
    private final ItemDto itemDto = new ItemDto(
            1L,
            "text",
            "good text",
            true,
            null);

    @SneakyThrows
    @Test
    void testItemDto() {
        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("text");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("good text");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathArrayValue("$.comments").isEqualTo(null);
        assertThat(result).extractingJsonPathNumberValue("$.request_id").isEqualTo(null);
    }
}
