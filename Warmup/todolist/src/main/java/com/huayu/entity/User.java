package com.huayu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * user实体类
 */
@Data
@TableName("tb_user")
public class User {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 密码，加密存储
     */
    private String password;

    /**
     * 用户账号
     */
    private String username;

}
