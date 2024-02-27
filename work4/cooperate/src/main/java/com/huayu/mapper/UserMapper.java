package com.huayu.mapper;

import com.huayu.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * user数据层
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
