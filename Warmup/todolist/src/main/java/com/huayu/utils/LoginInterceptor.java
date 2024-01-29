package com.huayu.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


/**
 * 登录校验的拦截器
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * 访问特定页面前，获取用户登录信息，判断用户登录状态
     *
     * @param request 用户的请求信息
     * @param response 返回的响应
     * @param handler 获取用户信息的工具
     * @throws Exception 抛出的异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //线程变量存在用户即成功，否则访问失败
        if (UserHolder.getUser() == null) {
            response.setStatus(401);
            return false;
        }
        return true;
    }
}
