package ru.practicum.item;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.config.PersistenceConfig;
import ru.practicum.user.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Transactional
@ExtendWith({SpringExtension.class})
@AllArgsConstructor(onConstructor_ = @Autowired)
@SpringJUnitConfig({PersistenceConfig.class, UserServiceImpl.class, ItemServiceImpl.class})
public class ItemServiceImplTest {


    private final EntityManager entityManager;

    private final ItemService service;

    private final UserService userService;

    @Test
    public void save() {
        ItemDto itemDto = new ItemDto();
        UserDto userDto = makeUserDto("some@email.com", "Пётр", "Иванов", LocalDate.of(1998, 2, 11));

        userService.saveUser(userDto);

        itemDto.setUrl("www.leningradspb.ru");
        itemDto.setTags(new HashSet<>());

        TypedQuery<User> userTypedQuery = entityManager.createQuery("select u from User u where u.email=:email", User.class);
        User user = userTypedQuery.setParameter("email", userDto.getEmail()).getSingleResult();

        service.addNewItem(user.getId(), itemDto);


        TypedQuery<Item> itemTypedQuery = entityManager.createQuery("select i from Item i where i.url=:url", Item.class);
        Item item = itemTypedQuery.setParameter("url", itemDto.getUrl()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getUrl(), equalTo(itemDto.getUrl()));

    }

    private UserDto makeUserDto(String email, String firstName, String lastName, LocalDate localDate) {
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setState(UserState.ACTIVE);
        dto.setDateOfBirth(String.valueOf(localDate));
        return dto;
    }

}
