package com.huayu.mapper;

import com.huayu.domain.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * blog数据层
 */
@Mapper
public interface BlogMapper extends BaseMapper<Blog> {

}
