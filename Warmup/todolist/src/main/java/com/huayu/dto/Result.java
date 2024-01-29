package com.huayu.dto;

import lombok.Data;

/**
 * 数据的返回对象
 */
@Data
public class Result {

    private Integer code;
    private String msg;
    private String token;
    private Object data;

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Result(Integer code, String msg, Object data, String token) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.token = token;
    }
}
