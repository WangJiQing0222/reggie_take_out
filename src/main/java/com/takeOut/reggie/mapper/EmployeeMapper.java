package com.takeOut.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.takeOut.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 小飞侠NO.1
 * @startTime 16:54:31
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
