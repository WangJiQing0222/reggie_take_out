package com.takeOut.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.takeOut.reggie.common.R;
import com.takeOut.reggie.entity.ShoppingCart;
import com.takeOut.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    @CacheEvict(value = "shoppingCart", allEntries = true)
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart, HttpSession session) {
        log.info("购物车数据:{}", shoppingCart);

        ShoppingCart cartServiceOne = shoppingCartService.add(shoppingCart, session);

        return R.success(cartServiceOne);
    }

    /**
     * 减少菜品或套餐数量
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    @CacheEvict(value = "shoppingCart", allEntries = true)
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart, HttpSession session) {
        log.info("减少套餐");

        ShoppingCart cartServiceOne = shoppingCartService.sub(shoppingCart, session);

        return R.success(cartServiceOne);
    }

    /**
     * 查看购物车
     *
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "shoppingCart", key = "'sC'")
    public R<List<ShoppingCart>> list(HttpSession session) {
        log.info("查看购物车...");

        List<ShoppingCart> list = shoppingCartService.list(session);

        return R.success(list);
    }

    /**
     * 清空购物车
     *
     * @return
     */
    @DeleteMapping("clean")
    @CacheEvict(value = "shoppingCart", allEntries = true)
    public R<String> chean(HttpSession session) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, session.getAttribute("user"));

        shoppingCartService.remove(queryWrapper);

        return R.success("清空购物车成功");
    }
}
