package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = repository.findAll();
        return UserMapper.mapToUserDto(users);
    }

    @Transactional
    @Override
    public UserDto saveUser(UserDto userDto) {
        List<User> users = repository.findAll();
        User user = repository.save(UserMapper.mapToNewUser(userDto));
        return UserMapper.mapToUserDto(user);
    }
}