package com.takeOut.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeOut.reggie.entity.Employee;
import com.takeOut.reggie.mapper.EmployeeMapper;
import com.takeOut.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author 小飞侠NO.1
 * @startTime 16:56:16
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
