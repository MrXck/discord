package com.discord.gateway.pojo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xck
 */
@Data
public class User {

    private Long id;
    private String username;

    private String password;
    private String avatar;

    private String phone;
    private String nickname;
    private String signature;
    private Integer sex;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private List<String> label;
}
