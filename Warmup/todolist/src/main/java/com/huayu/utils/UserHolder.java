package com.huayu.utils;

import com.huayu.dto.UserDTO;

/**
 * ThreadLocal方法的封装工具类
 */
public class UserHolder {
    private static final ThreadLocal<UserDTO> tl = new ThreadLocal<>();

    public static void saveUser(UserDTO userId) {
        tl.set(userId);
    }

    public static UserDTO getUser() {
        return tl.get();
    }

    public static void removeUser() {
        tl.remove();
    }
}
