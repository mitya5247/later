package ru.practicum.item;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.user.UserDto;
import ru.practicum.user.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashSet;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
// @SpringJUnitWebConfig() если хотим использовать web - конфигурацию (контекст с переданными бинами)
public class ItemComtrollerTest {

    @Mock
    ItemService service;

    private final ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    ItemController controller;

    private MockMvc mvc;

    @Test
    public void save() throws Exception {
        UserDto userDto = new UserDto();

        userDto.setId(1L);
        userDto.setEmail("some@email.com");
        userDto.setLastName("Иванов");
        userDto.setFirstName("Петр");
        userDto.setDateOfBirth(String.valueOf(LocalDate.of(1998, 3, 8)));
        userDto.setRegistrationDate("2022.07.03 19:55:00");

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setUrl("www.leningradspb.ru");
        itemDto.setTags(new HashSet<>());
        itemDto.setUserId(userDto.getId());

        Mockito.when(service.addNewItem(1, itemDto))
                .thenReturn(itemDto);

        mvc = MockMvcBuilders.standaloneSetup(controller).build();

        mvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .header("X-Later-User-Id", userDto.getId())
                .content(mapper.writeValueAsString(itemDto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.userId", is(itemDto.getUserId()), Long.class))
                .andExpect(jsonPath("$.url", is(itemDto.getUrl()), String.class));


    }
}
