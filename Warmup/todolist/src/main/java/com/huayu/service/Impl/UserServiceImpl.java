package com.huayu.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huayu.dto.LoginFormDTO;
import com.huayu.dto.Result;
import com.huayu.dto.UserDTO;
import com.huayu.entity.User;
import com.huayu.exception.BusinessException;
import com.huayu.mapper.UserMapper;
import com.huayu.service.IUserService;
import com.huayu.utils.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.huayu.utils.Code.*;
import static com.huayu.utils.RedisConstans.*;

/**
 * user的业务层实现实体类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result register(LoginFormDTO loginFormDTO) {
        try {
            creatUserWithPassword(loginFormDTO);
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new BusinessException(BUSINESS_ERR, "创建用户失败,该用户名已经被注册,请注意使用合法的字符以及账号密码长度");
        }
        return new Result(SUCCESS, "创建用户成功");
    }

    private User creatUserWithPassword(LoginFormDTO loginForm) throws SQLIntegrityConstraintViolationException {
        User user = new User();
        user.setUsername(loginForm.getUsername());
        user.setPassword(loginForm.getPassword());
        try {
            save(user);
        } catch (Exception e) {
            throw new SQLIntegrityConstraintViolationException();
        }
        return user;
    }

    @Override
    public Result login(LoginFormDTO loginForm) {
        String password = loginForm.getPassword();
        String username = loginForm.getUsername();
        User user = null;
        try {
            user = query().eq("username", username).one();
        } catch (MybatisPlusException e) {
            throw new BusinessException(BUSINESS_ERR, "用户不存在");
        }
        //判断密码是否与用户名一致
        if (!password.equals(user.getPassword())) {
            return new Result(FAIL, "账号密码错误");
        }
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        //创建一个随机的token
        String token = UUID.randomUUID().toString(true);
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                CopyOptions.create().setIgnoreNullValue(true).setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        //将用户信息保存到redis用于登录校验
        stringRedisTemplate.opsForHash().putAll(LOGIN_USER_KEY + token, userMap);
        //设置用户登录过期时间
        stringRedisTemplate.expire(LOGIN_USER_KEY + token, LOGIN_USER_TTL, TimeUnit.MINUTES);
        return new Result(SUCCESS, "登录成功", userDTO, token);
    }
}
