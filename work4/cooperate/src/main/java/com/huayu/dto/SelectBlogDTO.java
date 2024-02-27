package com.huayu.dto;

import com.huayu.domain.Blog;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 查询单篇文章的返回对象
 */
@Data
@AllArgsConstructor
public class SelectBlogDTO {

    private Blog blog;

    private Long count;
}
