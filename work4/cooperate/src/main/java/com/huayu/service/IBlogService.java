package com.huayu.service;

import com.huayu.domain.Blog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.huayu.dto.Result;

/**
 * blog服务层
 */
public interface IBlogService extends IService<Blog> {

    Result saveBlog(Blog blog);

    Result getBlog(Long id);

    Result likeBlog(Long id);

    Result getMyBlogs();

    Result getMyLike();

    Result getNewBlog(Long max, Integer offset);

    Result getHotBlog(Integer current);
}
