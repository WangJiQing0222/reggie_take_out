package com.takeOut.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeOut.reggie.entity.ShoppingCart;

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
    ShoppingCart add(ShoppingCart shoppingCart);


}
