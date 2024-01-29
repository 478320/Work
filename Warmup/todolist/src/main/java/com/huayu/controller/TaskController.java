package com.huayu.controller;

import com.huayu.dto.Result;
import com.huayu.entity.Task;
import com.huayu.service.ITaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * task的表现层对象
 */
@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private ITaskService taskService;

    /**
     * 添加代办的接口方法
     *
     * @param task 用户传入的代办信息
     */
    @PostMapping
    public Result saveTask(@RequestBody Task task, HttpServletRequest request) {
        return taskService.saveTask(task, request);
    }

    /**
     * 查询已完成/未完成代办的接口方法
     *
     * @param request 携带查询头的用户唯一token信息
     * @param status  查询的任务的状态
     */
    @GetMapping("/{status}")
    public Result listTask(@PathVariable("status") Integer status, HttpServletRequest request) {
        return taskService.listTask(status, request);
    }

    /**
     * 更新代办状态的接口方法
     *
     * @param id      要更新的代办的唯一id
     * @param request 携带查询头的用户唯一token信息
     * @param task    携带更改用户状态的请求体
     */
    @PutMapping("/{id}")
    public Result updateStatus(@PathVariable("id") Long id, HttpServletRequest request, @RequestBody Task task) {
        return taskService.updateStatus(id, request, task);
    }

    /**
     * 删除代办的接口方法
     *
     * @param id      要删除的代办的唯一id
     * @param request 携带查询头的用户唯一token信息
     */
    @CrossOrigin(origins = "*", methods = {RequestMethod.PUT, RequestMethod.OPTIONS, RequestMethod.DELETE, RequestMethod.GET, RequestMethod.POST}, allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public Result removeTask(@PathVariable("id") Long id, HttpServletRequest request) {
        return taskService.removeTask(id, request);
    }
}
