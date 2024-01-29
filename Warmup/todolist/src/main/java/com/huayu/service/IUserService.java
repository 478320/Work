package com.huayu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huayu.dto.LoginFormDTO;
import com.huayu.dto.Result;
import com.huayu.entity.User;
import jakarta.servlet.http.HttpServletResponse;

/**
 * user业务层接口
 */
public interface IUserService extends IService<User> {

    Result register(LoginFormDTO loginFormDTO);

    Result login(LoginFormDTO loginForm);
}
