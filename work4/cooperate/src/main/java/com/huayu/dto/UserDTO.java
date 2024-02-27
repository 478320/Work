package com.huayu.dto;

import lombok.Data;

/**
 * user的数据输出对象
 */
@Data
public class UserDTO {

    private Long id;

    private String username;

    private String avatarUrl;

    private String motto;

    private Long blogCount;
}
