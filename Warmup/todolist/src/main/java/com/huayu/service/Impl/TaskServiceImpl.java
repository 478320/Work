package com.huayu.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huayu.dto.Data;
import com.huayu.dto.Result;
import com.huayu.dto.UserDTO;
import com.huayu.entity.Task;
import com.huayu.exception.BusinessException;
import com.huayu.mapper.TaskMapper;
import com.huayu.service.ITaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.huayu.utils.Code.*;
import static com.huayu.utils.RedisConstans.*;

/**
 * task的业务层实现实体类
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements ITaskService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public Result saveTask(Task task, HttpServletRequest request) {
        //从请求头中获取token信息
        String token = request.getHeader("authorization");
        //根据token获取用户名
        Map<Object, Object> usermap = stringRedisTemplate.opsForHash().entries(LOGIN_USER_KEY + token);
        UserDTO userDTO = BeanUtil.mapToBean(usermap, UserDTO.class, true, CopyOptions.create());
        String username = userDTO.getUsername();
        //设置代办的创建者
        task.setUsername(username);
        try {
            taskMapper.insert(task);
        } catch (Exception e) {
            throw new BusinessException(BUSINESS_ERR, "代办字数过多，请分批添加");
        }
        stringRedisTemplate.delete(CACHE_TASK_KEY + username + 0);
        return new Result(SUCCESS, "新增代办成功");
    }

    @Override
    public Result listTask(Integer status, HttpServletRequest request) {
        //从请求头中获取token信息{
        String token = request.getHeader("authorization");
        //根据token获取用户名
        Map<Object, Object> usermap = stringRedisTemplate.opsForHash().entries(LOGIN_USER_KEY + token);
        UserDTO userDTO = BeanUtil.mapToBean(usermap, UserDTO.class, true, CopyOptions.create());
        String username = userDTO.getUsername();
        //尝试从缓存中获取代办信息
        String taskJson = stringRedisTemplate.opsForValue().get(CACHE_TASK_KEY + username + status);
        //获取成功连带信息数量一起返回
        if (StrUtil.isNotBlank(taskJson)) {
            List<Task> taskList = JSONUtil.parseArray(taskJson).toList(Task.class);
            Integer total = taskList.size();
            Data data = new Data(taskList, total);
            return new Result(SUCCESS, "查询成功", data);
        }
        //获取失败判断获取的是否是空字符串，空字符则返回空，防止缓存穿透
        if (taskJson != null) {
            return null;
        }
        //从数据库查询代办信息
        LambdaQueryWrapper<Task> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Task::getUsername, username).eq(Task::getStatus, status);
        List<Task> taskList = taskMapper.selectList(lqw);
        //数据库中无代办信息返回空，并保存空字符串于缓存，防止缓存穿透
        if (taskList.isEmpty()) {
            stringRedisTemplate.opsForValue().set(CACHE_TASK_KEY + username + status, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        //数据库有信息则携带对应的信息数量返回
        stringRedisTemplate.opsForValue().set(CACHE_TASK_KEY + username + status, JSONUtil.toJsonStr(taskList), CACHE_TASK_TTL, TimeUnit.MINUTES);
        Integer total = taskList.size();
        Data data = new Data(taskList, total);
        return new Result(SUCCESS, "查询成功", data);
    }

    @Override
    public Result updateStatus(Long id, HttpServletRequest request, Task task) {
        //从请求头中获取token信息
        String token = request.getHeader("authorization");
        //根据token获取用户名
        Map<Object, Object> usermap = stringRedisTemplate.opsForHash().entries(LOGIN_USER_KEY + token);
        UserDTO userDTO = BeanUtil.mapToBean(usermap, UserDTO.class, true, CopyOptions.create());
        String username = userDTO.getUsername();
        //将id信息传递给更改代办的代办对象，便于后续的更新
        task.setTid(id);
        //获取原有的代办对象,得到原有的status用于删除缓存
        Task task1 = taskMapper.selectById(id);
        updateById(task);
        if (task1 == null) {
            return new Result(FAIL_NULL, "要更新的代办不存在");
        }
        Integer status = task1.getStatus();
        //删除对应的缓存，增强数据的一致性
        stringRedisTemplate.delete(CACHE_TASK_KEY + username + status);
        return new Result(SUCCESS, "更改成功");
    }

    @Override
    public Result removeTask(Long id, HttpServletRequest request) {
        //从请求头中获取token信息
        String token = request.getHeader("authorization");
        //根据token获取用户名
        Map<Object, Object> usermap = stringRedisTemplate.opsForHash().entries(LOGIN_USER_KEY + token);
        UserDTO userDTO = BeanUtil.mapToBean(usermap, UserDTO.class, true, CopyOptions.create());
        String username = userDTO.getUsername();
        Task task1 = taskMapper.selectById(id);
        if (task1 == null) {
            return new Result(FAIL_NULL, "要删除的代办不存在");
        }
        Integer status1 = task1.getStatus();
        taskMapper.deleteById(id);
        //删除对应的缓存，增强数据的一致性
        stringRedisTemplate.delete(CACHE_TASK_KEY + username + status1);
        return new Result(SUCCESS, "删除成功");
    }
}
