package com.huayu.service;

import com.huayu.domain.BlogComments;
import com.baomidou.mybatisplus.extension.service.IService;
import com.huayu.dto.Result;

/**
 * blogComments服务类
 */
public interface IBlogCommentsService extends IService<BlogComments> {

    Result getBlogComments(Long id);

    Result saveBlogComments(BlogComments blogComments);

    Result replyBlogComments(BlogComments blogComments);

    Result likeBlogComments(Long id);
}
