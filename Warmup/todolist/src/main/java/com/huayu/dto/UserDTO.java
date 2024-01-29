package com.huayu.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * user的数据输出对象
 */
@Data
public class UserDTO {

    private String username;

}
