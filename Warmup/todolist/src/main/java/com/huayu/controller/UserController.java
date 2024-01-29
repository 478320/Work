package com.huayu.controller;

import com.huayu.dto.LoginFormDTO;
import com.huayu.dto.Result;
import com.huayu.entity.User;
import com.huayu.service.IUserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * user的表现层对象
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 创建账号的接口方法
     *
     * @param loginForm 注册的用户信息
     */
    @PostMapping("/register")
    public Result register(@RequestBody LoginFormDTO loginForm) {
        return userService.register(loginForm);
    }

    /**
     * 登录账号的接口方法
     *
     * @param loginForm 登录的用户信息
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginForm) {
        return userService.login(loginForm);
    }
}
