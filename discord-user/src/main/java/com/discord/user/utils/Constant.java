package com.discord.user.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author xck
 */
public class Constant {

    public static final String PATH = System.getProperty("user.dir") + "/files/";
    public static final List<String> SUFFIX_WHITE_LIST = new ArrayList<>(Arrays.asList("jpg", "jpeg", "png", "gif"));

    public static final String LOGIN_ERROR = "用户名或密码错误";
    public static final String USERNAME_ALREADY_ERROR = "用户名已存在";

    public static final String FILE_NOT_FOUND = "文件不存在";
}
