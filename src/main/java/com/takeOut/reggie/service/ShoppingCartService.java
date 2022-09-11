package com.takeOut.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeOut.reggie.entity.ShoppingCart;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author 小飞侠NO.1
 * @startTime 16:51:39
 */
public interface ShoppingCartService extends IService<ShoppingCart> {
    /**
     * 添加到购物车，或点击购物车的加号按钮，增加物品数量
     * @param shoppingCart
     * @return
     */
    ShoppingCart add(ShoppingCart shoppingCart, HttpSession session);

    /**
     * 减少购物车该物品数量，点击购物车的减号按钮，减少物品数量
     * 限制不能减少到零以下
     * @param shoppingCart
     * @param session
     * @return
     */
    ShoppingCart sub(ShoppingCart shoppingCart, HttpSession session);

    /**
     * 查看购物车
     *
     * @return
     */
    List<ShoppingCart> list(HttpSession session);
}
