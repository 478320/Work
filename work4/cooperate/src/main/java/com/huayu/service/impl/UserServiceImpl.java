package com.huayu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huayu.domain.Blog;
import com.huayu.domain.User;
import com.huayu.dto.LoginUser;
import com.huayu.dto.Result;
import com.huayu.dto.UserDTO;
import com.huayu.exception.BusinessException;
import com.huayu.mapper.BlogMapper;
import com.huayu.mapper.UserMapper;
import com.huayu.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huayu.utils.JwtUtil;
import com.huayu.utils.UploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.huayu.utils.Code.*;
import static com.huayu.utils.RedisConstans.LOGIN_USER_KEY;

/**
 * user服务层实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BlogMapper blogMapper;

    @Override
    public Result login(User user) {
        //AuthenticationManager authenticate进行用户认证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //如果认证没通过，给出对应的提示
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("登录失败");
        }
        //如果认证通过了，使用userid生成一个jwt jwt存入ResponseResult返回
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userid = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userid);
        Map<String,String> map = new HashMap<>();
        map.put("token",jwt);
        //把完整的用户信息存入redis  userid作为key
        String loginUserStr = JSONUtil.toJsonStr(loginUser);
        stringRedisTemplate.opsForValue().set(LOGIN_USER_KEY+userid,loginUserStr,24, TimeUnit.HOURS);
        return new Result(200,"登录成功",map);
    }


    @Override
    public Result register(User user) {
        try {
            creatUserWithPassword(user);
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new BusinessException(BUSINESS_ERR, "创建用户失败,该用户名已经被注册,请注意使用合法的字符以及账号密码长度");
        }
        return new Result(SUCCESS, "创建用户成功");
    }

    private User creatUserWithPassword(User user) throws SQLIntegrityConstraintViolationException {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(user.getPassword());
        user.setPassword(encode);
        try {
            save(user);
        } catch (Exception e) {
            throw new SQLIntegrityConstraintViolationException();
        }
        return user;
    }

    @Override
    public Result getUserById(Long id) {
        User user = userMapper.selectById(id);
        //封装user对象防止用户密码泄露
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        return new Result(SUCCESS,"",userDTO);
    }

    @Transactional
    @Override
    public Result updateUser(User user) {
        LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long id = principal.getUser().getId();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(user.getPassword());
        user.setPassword(encode);
        user.setId(id);
        try {
            userMapper.updateById(user);
        } catch (Exception e) {
            throw new BusinessException(BUSINESS_ERR, "更改用户失败,该用户名已经被注册,请注意使用合法的字符以及账号密码长度");
        }
        stringRedisTemplate.delete(LOGIN_USER_KEY+id);
        return new Result(SUCCESS,"更改成功",user.getAvatarUrl());
    }

    @Override
    public Result getMe() {
        LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principal.getUser();
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        //查询我的文章的数量
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Blog::getUserId,user.getId());
        Long blogCount = blogMapper.selectCount(queryWrapper);
        userDTO.setBlogCount(blogCount);
        return new Result(SUCCESS,"",userDTO);
    }

    @Override
    public Result uploadAvatar(MultipartFile file) {
        LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principal.getUser();
        //file校验
        if (file.isEmpty()) {
            return new Result(FAIL,"上传头像失败");
        }
        try {
            String fileUrl = UploadUtil.uploadImage(file);
            user.setAvatarUrl(fileUrl);
            userMapper.updateById(user);
            return new Result(SUCCESS,"上传头像成功",fileUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
