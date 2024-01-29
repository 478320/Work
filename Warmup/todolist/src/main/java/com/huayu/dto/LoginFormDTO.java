package com.huayu.dto;

import lombok.Data;

/**
 * user数据的接收对象
 */
@Data
public class LoginFormDTO {

    private String username;

    private String password;
}
