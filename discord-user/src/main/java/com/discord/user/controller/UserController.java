package com.discord.user.controller;

import com.discord.user.dto.RegisterUserDTO;
import com.discord.user.dto.UpdateUserDTO;
import com.discord.user.dto.UserDTO;
import com.discord.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public UserDTO login(@RequestBody @Valid UserDTO userDTO) {
        return userService.login(userDTO);
    }

    @PostMapping("/update")
    public void update(@RequestBody @Valid UpdateUserDTO updateUserDTO) {
        userService.updateUser(updateUserDTO);
    }

    @PostMapping("/register")
    public void register(@RequestBody @Valid RegisterUserDTO registerUser) {
        userService.register(registerUser);
    }
}
