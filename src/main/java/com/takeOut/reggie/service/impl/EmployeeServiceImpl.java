package com.takeOut.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeOut.reggie.common.R;
import com.takeOut.reggie.entity.Employee;
import com.takeOut.reggie.mapper.EmployeeMapper;
import com.takeOut.reggie.service.EmployeeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 小飞侠NO.1
 * @startTime 16:56:16
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    /**
     *  登录
     * @param request
     * @param employee
     * @return
     */
    @Override
    public R<Employee> login(HttpServletRequest request, Employee employee) {

        /*  1、将页面提交的密码password进行md5加密处理
            2、根据页面提交的用户名username查询数据库
            3、如果没有查到则返回登录失败结果
            4、密码比对，如果不一致则返回登录失败结果
            5、查看员工状态，如果为已禁用状态，则返回已禁用结果
            6、登陆成功，将员工id存入session并返回登录成功结果
        */

        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = this.getOne(queryWrapper);

        //3、如果没有查到则返回登录失败结果
        if(emp == null){
            return R.error("登录失败，数据库中没有该员工");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败，密码错误");
        }

        //5、查看员工状态，如果为已禁用状态，则返回已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        //6、登陆成功，将员工id存入session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());


        return R.success(emp);
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page<Employee> page(int page, int pageSize, String name) {

        //构造分页构造器
        Page<Employee> pageInfo = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加分页条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        this.page(pageInfo, queryWrapper);


        return pageInfo;
    }
}
