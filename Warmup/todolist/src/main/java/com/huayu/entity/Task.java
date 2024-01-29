package com.huayu.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * task的实体类
 */
@Data
@TableName("tb_task")
public class Task {

    /**
     * 任务id，主键
     */
    @TableId
    private Long tid;

    /**
     * 文本内容
     */
    private String content;

    /**
     * 文本对应的用户名
     */
    private String username;

    /**
     * 文本的状态，0代表未完成，1代表已完成
     */
    private Integer status;

    /**
     * 用户选择代办开始的时间
     */
    @JsonFormat(pattern = "yyyy/M/d H:mm", timezone = "GMT+8")
    @TableField(value = "start_time")
    private LocalDateTime startTime;

    /**
     * 用户选择代办结束的时间
     */
    @JsonFormat(pattern = "yyyy/M/d H:mm", timezone = "GMT+8")
    @TableField(value = "end_time")
    private LocalDateTime endTime;
}
