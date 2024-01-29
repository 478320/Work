package com.huayu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huayu.dto.Result;
import com.huayu.entity.Task;
import jakarta.servlet.http.HttpServletRequest;

/**
 * task业务层接口
 */
public interface ITaskService extends IService<Task> {

    Result saveTask(Task task, HttpServletRequest request);

    Result listTask(Integer status, HttpServletRequest request);

    Result updateStatus(Long id, HttpServletRequest request, Task task);

    Result removeTask(Long id, HttpServletRequest request);
}
