package com.huayu.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.huayu.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.huayu.utils.RedisConstans.LOGIN_USER_KEY;
import static com.huayu.utils.RedisConstans.LOGIN_USER_TTL;

/**
 * 刷新用户登录有效时间的拦截器
 */
@Component
public class RefreshTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 访问结束释放线程变量，防止内存泄露
     *
     * @param request 用户的请求信息
     * @param response 返回的响应
     * @param handler 获取用户信息的工具
     * @param ex 异常
     * @throws Exception 抛出的异常
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }

    /**
     * 刷新用户的登录有效时间，并传递用户token给线程变量
     *
     * @param request 用户的请求信息
     * @param response 返回的响应
     * @param handler 获取用户信息的工具
     * @throws Exception 抛出的异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取token
        String token = request.getHeader("authorization");
        //获取为空交由下一级拦截器处理
        if (StrUtil.isBlank(token)) {
            return true;
        }
        //根据token获取用户信息
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(LOGIN_USER_KEY + token);
        //用户信息为空交由下一级拦截器处理
        if (userMap.isEmpty()) {
            return true;
        }
        //获取user信息并保存到线程变量中
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
        UserHolder.saveUser(userDTO);
        //刷新用户登录有效时间
        stringRedisTemplate.expire(LOGIN_USER_KEY + token, LOGIN_USER_TTL, TimeUnit.MINUTES);
        return true;
    }
}
