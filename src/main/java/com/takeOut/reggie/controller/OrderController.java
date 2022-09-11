package com.takeOut.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeOut.reggie.common.R;
import com.takeOut.reggie.dto.OrderDto;
import com.takeOut.reggie.entity.Orders;
import com.takeOut.reggie.service.OrderDetailService;
import com.takeOut.reggie.service.OrderService;
import com.takeOut.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

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

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 用户下单
     *
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders, HttpSession session) {
        log.info("订单数据:{}", orders);
        orderService.submit(orders, session);
        return R.success("下单成功");
    }

    /**
     * 订单展示
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, Long number, LocalDateTime beginTime, LocalDateTime endTime) {
        log.info("订单显示:{}");

        Page<OrderDto> orderDtoPage = orderService.page(page, pageSize, number, beginTime, endTime);


        return R.success(orderDtoPage);
    }

    /**
     * 修改订单状态
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Orders orders) {
        log.info("修改订单状态");

        orderService.updateById(orders);

        return R.success("已派送");
    }

    /**
     * 用户页面
     * @param page
     * @param pageSize
     * @param session
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize, HttpSession session) {
        log.info("userPage...");

        Page<OrderDto> orderDtoPage = orderService.userPage(page, pageSize, session);

        return R.success(orderDtoPage);
    }

    /**
     * 再来一单
     * @param orders
     * @param session
     * @return
     */
    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders, HttpSession session) {
        log.info("再来一单：{}", orders);

        boolean flag = orderService.again(orders, session);

        return flag == true ? R.success("再次下单成功") : R.error("再次下单失败");
    }

}
