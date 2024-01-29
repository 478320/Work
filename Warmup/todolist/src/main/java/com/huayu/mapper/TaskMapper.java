package com.huayu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huayu.entity.Task;
import org.apache.ibatis.annotations.Mapper;

/**
 * task数据层
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {
}
