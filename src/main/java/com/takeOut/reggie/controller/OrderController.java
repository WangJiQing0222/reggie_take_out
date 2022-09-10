package com.takeOut.reggie.controller;

import com.takeOut.reggie.common.R;
import com.takeOut.reggie.entity.Orders;
import com.takeOut.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author 小飞侠NO.1
 * @startTime 18:06:29
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders, HttpSession session){
        log.info("订单数据:{}", orders);
        orderService.submit(orders, session);
        return R.success("下单成功");
    }

}
