package com.takeOut.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.takeOut.reggie.common.R;
import com.takeOut.reggie.entity.Employee;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 小飞侠NO.1
 * @startTime 16:55:19
 */
public interface EmployeeService extends IService<Employee> {
    /**
     *  登录
     * @param request
     * @param employee
     * @return
     */
    R<Employee> login(HttpServletRequest request, Employee employee);

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    Page<Employee> page(int page, int pageSize, String name);

}
