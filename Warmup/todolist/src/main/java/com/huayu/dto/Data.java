package com.huayu.dto;

import lombok.AllArgsConstructor;

/**
 * 数据的返回对象
 */
@lombok.Data
@AllArgsConstructor
public class Data {

    private Object item;

    private Integer total;
}
