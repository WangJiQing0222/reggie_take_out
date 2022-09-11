package com.takeOut.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeOut.reggie.common.BaseContext;
import com.takeOut.reggie.entity.ShoppingCart;
import com.takeOut.reggie.mapper.ShoppingCartMapper;
import com.takeOut.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author 小飞侠NO.1
 * @startTime 16:52:04
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    /**
     * 用户下单
     * @param shoppingCart
     * @return
     */
    public ShoppingCart add(ShoppingCart shoppingCart) {
        //设置用户id，指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        //查询当前菜品或套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());

        if (dishId != null) {
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart cartServiceOne = this.getOne(queryWrapper);
        if (cartServiceOne != null) {
            //如果存在，则在原来的数量上加一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            this.updateById(cartServiceOne);
        } else {
            //如果不存在，则添加到购物车，数量默认就是一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            this.save(shoppingCart);
            cartServiceOne = shoppingCart;//因为save完就会shoppingCart的id已经被赋值了
        }

        return cartServiceOne;
    }
}
