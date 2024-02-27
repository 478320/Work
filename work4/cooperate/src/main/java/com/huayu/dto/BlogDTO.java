package com.huayu.dto;

import com.huayu.domain.Blog;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 文章数据的返回对象
 */
@Data
@AllArgsConstructor
public class BlogDTO {

    private List<Blog> blogList;

    private Integer count;

}
