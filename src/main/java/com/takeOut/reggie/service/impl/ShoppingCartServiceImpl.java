package com.takeOut.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeOut.reggie.entity.ShoppingCart;
import com.takeOut.reggie.mapper.ShoppingCartMapper;
import com.takeOut.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @author 小飞侠NO.1
 * @startTime 16:52:04
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
