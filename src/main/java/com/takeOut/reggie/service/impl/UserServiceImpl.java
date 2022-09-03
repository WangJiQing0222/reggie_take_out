package com.takeOut.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeOut.reggie.entity.User;
import com.takeOut.reggie.mapper.UserMapper;
import com.takeOut.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author 小飞侠NO.1
 * @startTime 10:19:19
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
