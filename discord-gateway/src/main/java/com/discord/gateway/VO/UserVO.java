package com.discord.gateway.VO;

import com.discord.gateway.pojo.User;
import lombok.Data;

@Data
public class UserVO {
    private User user;
    private String token;
}
