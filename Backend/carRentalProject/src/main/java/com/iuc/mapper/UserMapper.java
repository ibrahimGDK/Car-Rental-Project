package com.iuc.mapper;

import com.iuc.dto.UserDTO;
import com.iuc.entities.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO userToUserDTO(User user);

    List<UserDTO> map(List<User> userList);
}
