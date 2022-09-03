package com.takeOut.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeOut.reggie.entity.OrderDetail;
import com.takeOut.reggie.mapper.OrderDetailMapper;
import com.takeOut.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author 小飞侠NO.1
 * @startTime 18:04:48
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
