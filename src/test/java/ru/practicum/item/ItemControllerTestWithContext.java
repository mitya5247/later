package ru.practicum.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.config.AppConfig;
import ru.practicum.config.PersistenceConfig;
import ru.practicum.config.WebConfig;
import ru.practicum.user.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashSet;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringJUnitWebConfig({WebConfig.class, ItemControllerTestConfig.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTestWithContext {

    MockMvc mvc;

    @Mock
    ItemService service;

    ObjectMapper mapper = new ObjectMapper();

    ItemDto itemDto;

    UserDto userDto;

    private final ItemController controller;

    @BeforeEach
    public void setUp(WebApplicationContext context) {
        itemDto = new ItemDto();
        userDto = new UserDto();

        userDto.setId(1L);
        userDto.setEmail("some@email.com");
        userDto.setLastName("Иванов");
        userDto.setFirstName("Петр");
        userDto.setDateOfBirth(String.valueOf(LocalDate.of(1998, 3, 8)));
        userDto.setRegistrationDate("2022.07.03 19:55:00");

        itemDto.setId(1L);
        itemDto.setUrl("www.leningradspb.ru");
        itemDto.setTags(new HashSet<>());
        itemDto.setUserId(userDto.getId());

        mvc = MockMvcBuilders.webAppContextSetup(context).build();


    }

    @Test
    public void save() throws Exception {

        Mockito.when(service.addNewItem(userDto.getId(), itemDto))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Later-User-Id", userDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.userId", is(itemDto.getUserId()), Long.class))
                .andExpect(jsonPath("$.url", is(itemDto.getUrl()), String.class));
    }
}
