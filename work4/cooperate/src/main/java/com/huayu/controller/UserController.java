package com.huayu.controller;

import com.huayu.domain.User;
import com.huayu.dto.Result;
import com.huayu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * user的表现层对象
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 登录
     *
     * @param user 登录的用户
     * @return 用户登录凭证
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        //登录
        return userService.login(user);
    }

    /**
     * 注册
     *
     * @param user 注册的用户
     */
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        return userService.register(user);
    }

    /**
     * 查询某个用户
     *
     * @param id 用户的id
     * @return 用户的信息
     */
    @GetMapping("/{id}")
    public Result getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    /**
     * 更新用户的信息
     *
     * @param user 要更新的用户信息
     */
    @PutMapping
    public Result updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    /**
     * 获取我的信息
     *
     * @return 登录用户的信息
     */
    @GetMapping("/me")
    public Result getMe() {
        return userService.getMe();
    }

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return 头像文件的绝对路径
     */
    @PostMapping("/uploadAvatar")
    public Result uploadAvatar(MultipartFile file){
        return userService.uploadAvatar(file);
    }

}
