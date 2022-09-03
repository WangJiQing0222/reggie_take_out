package com.takeOut.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.takeOut.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 小飞侠NO.1
 * @startTime 10:17:42
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
