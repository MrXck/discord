package com.discord.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.discord.user.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
