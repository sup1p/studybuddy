package com.studybuddy.demostud.Service;




import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.Config.UserDto;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findUserByEmail(String email);

    List<UserDto> findAllUsers();
}