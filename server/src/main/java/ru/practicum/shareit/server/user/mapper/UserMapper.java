package ru.practicum.shareit.server.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.entity.User;

@UtilityClass
public class UserMapper {
    public static UserDto toUserDto(UserDto user, User initUser) {
        return new UserDto(user.getId() != null ? user.getId() : initUser.getId(), user.getName() != null ? user.getName() : initUser.getName(), user.getEmail() != null ? user.getEmail() : initUser.getEmail());
    }

    public static User dtoToUser(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }
}
