package ru.practicum.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class ItemControllerTestConfig {
    @Bean
    public ItemController controller() {
        return mock(ItemController.class);
    }
}
