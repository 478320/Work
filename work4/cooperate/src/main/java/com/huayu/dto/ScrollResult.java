package com.huayu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 滚动查询的返回对象
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class ScrollResult {
    private List<?> list;
    private Long minTime;
    private Integer offset;
}
