package com.studybuddy.demostud.Service;

import com.studybuddy.demostud.models.Role;
import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.repository.UserRepository;
import com.studybuddy.demostud.repository.RoleRepository;
import com.studybuddy.demostud.Config.UserDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceWork implements UserService {

    private static UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceWork(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static String getUsernameById(Long id) {
        return userRepository.findUsernameById(id);
    }

    @Override
    public void saveUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getNickname());
        user.setEmail(userDto.getEmail());
        // encrypt the password using spring security
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role role = roleRepository.findByName("ROLE_ADMIN");
        if(role == null){
            role = checkRoleExist();
        }
        user.setRoles(Arrays.asList(role));
        userRepository.save(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map((user) -> mapToUserDto(user))
                .collect(Collectors.toList());
    }

    private UserDto mapToUserDto(User user){
        UserDto userDto = new UserDto();
        String[] str = user.getUsername().split(" ");
        userDto.setNickname(str[0]);
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    private Role checkRoleExist(){
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        return roleRepository.save(role);
    }




}
