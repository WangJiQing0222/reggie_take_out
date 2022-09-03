package com.takeOut.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.takeOut.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 小飞侠NO.1
 * @startTime 18:00:17
 */
@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
