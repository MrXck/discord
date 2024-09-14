package com.discord.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.discord.user.dto.RegisterUserDTO;
import com.discord.user.dto.UpdateUserDTO;
import com.discord.user.dto.UserDTO;
import com.discord.user.pojo.User;

public interface UserService extends IService<User> {

    UserDTO login(UserDTO userDTO);

    void updateUser(UpdateUserDTO updateUserDTO);

    void register(RegisterUserDTO registerUser);
}
