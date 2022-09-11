package com.takeOut.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeOut.reggie.entity.ShoppingCart;
import com.takeOut.reggie.mapper.ShoppingCartMapper;
import com.takeOut.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 小飞侠NO.1
 * @startTime 16:52:04
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    /**
     * 添加到购物车，或点击购物车的加号按钮，增加物品数量
     * @param shoppingCart
     * @return
     */
    public ShoppingCart add(ShoppingCart shoppingCart, HttpSession session) {
        //设置用户id，指定当前是哪个用户的购物车数据
        Long currentId = (Long) session.getAttribute("user");
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

    /**
     * 减少购物车该物品数量，点击购物车的减号按钮，减少物品数量
     * @param shoppingCart
     * @param session
     * @return
     */
    public ShoppingCart sub(ShoppingCart shoppingCart, HttpSession session) {

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, session.getAttribute("user"));

        if(shoppingCart.getDishId() != null){//dishId不为空，说明是要减少菜品数量
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {//否则就是减少套餐数量
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart cartServiceOne = this.getOne(queryWrapper);

        if(cartServiceOne.getNumber() <= 0){//小于0的校验
            cartServiceOne.setNumber(0);
        }else{
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number - 1);
        }
        this.updateById(cartServiceOne);


        return cartServiceOne;
    }

    /**
     * 查看购物车
     *
     * @return
     */
    public List<ShoppingCart> list(HttpSession session) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, session.getAttribute("user"));
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = this.list(queryWrapper);

        return list;
    }
}
