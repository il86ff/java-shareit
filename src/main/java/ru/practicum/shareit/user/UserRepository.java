package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {
    private final Map<Long,User> users;
    private Long id = 0L;

    public UserRepository() {
        this.users = new HashMap<>();
    }

    public User create(UserDto userDto) {
        User newUser = UserMapper.dtoToUser(userDto);
        if (newUser.getId() == null) newUser.setId(++id);
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    public User update(User user, Long id) {
            users.put(id, user);
            return users.get(id);
    }
    public Optional<User> getById(Long id){
        return Optional.ofNullable(users.get(id));
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public User deleteUser(Long id) {
        return users.remove(id);
    }
}
