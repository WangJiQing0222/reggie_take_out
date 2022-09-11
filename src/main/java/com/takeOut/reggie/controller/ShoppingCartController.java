package com.takeOut.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.takeOut.reggie.common.BaseContext;
import com.takeOut.reggie.common.R;
import com.takeOut.reggie.entity.ShoppingCart;
import com.takeOut.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author 小飞侠NO.1
 * @startTime 16:52:56
 */
@RestController
@RequestMapping("shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加菜品或套餐购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("购物车数据:{}", shoppingCart);

        ShoppingCart cartServiceOne = shoppingCartService.add(shoppingCart);

        return R.success(cartServiceOne);
    }

    /**
     * 减少菜品或套餐数量
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        if(shoppingCart.getDishId() != null){//dishId不为空，说明是要减少菜品数量
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {//否则就是减少套餐数量
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

        if(cartServiceOne.getNumber() <= 0){//小于0的校验
            cartServiceOne.setNumber(0);
        }else{
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number - 1);
        }
        shoppingCartService.updateById(cartServiceOne);

        return R.success(cartServiceOne);
    }

    /**
     * 查看购物车
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(HttpSession session) {
        log.info("查看购物车...");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, session.getAttribute("user"));
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 清空购物车
     *
     * @return
     */
    @DeleteMapping("clean")
    public R<String> chean() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        shoppingCartService.remove(queryWrapper);

        return R.success("清空购物车成功");
    }
}
