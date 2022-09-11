package com.takeOut.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeOut.reggie.common.R;
import com.takeOut.reggie.dto.OrderDto;
import com.takeOut.reggie.entity.OrderDetail;
import com.takeOut.reggie.entity.Orders;
import com.takeOut.reggie.entity.ShoppingCart;
import com.takeOut.reggie.service.OrderDetailService;
import com.takeOut.reggie.service.OrderService;
import com.takeOut.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, Long number, LocalDateTime beginTime, LocalDateTime endTime) {
        log.info("订单显示:{}");

        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrderDto> orderDtoPage = new Page<>();

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(number != null, Orders::getNumber, number);
        queryWrapper.ge(beginTime != null, Orders::getOrderTime, beginTime);
        queryWrapper.le(endTime != null, Orders::getCheckoutTime, endTime);
        queryWrapper.orderByAsc(endTime != null, Orders::getCheckoutTime);

        orderService.page(pageInfo, queryWrapper);//查询orders到pageInfo

        BeanUtils.copyProperties(pageInfo, orderDtoPage, "records");

        //设置records含有orderDetail 订单明细
        List<Orders> records = pageInfo.getRecords();
        List<OrderDto> list = records.stream().map((item) -> {
            OrderDto orderDto = new OrderDto();
            BeanUtils.copyProperties(item, orderDto);

            if (item != null) {
                LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(OrderDetail::getOrderId, item.getId());

                orderDto.setOrderDetails(orderDetailService.list(wrapper));//设置orderDetails
            }

            return orderDto;
        }).collect(Collectors.toList());

        orderDtoPage.setRecords(list);//设置records

        return R.success(orderDtoPage);
    }

    @PutMapping
    public R<String> update(@RequestBody Orders orders) {
        log.info("修改订单状态");

        orderService.updateById(orders);

        return R.success("已派送");
    }

    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize, HttpSession session) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrderDto> orderDtoPage = new Page<>();

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, session.getAttribute("user"));
        orderService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, orderDtoPage, "records");

        List<Orders> records = pageInfo.getRecords();
        List<OrderDto> list = records.stream().map((item) -> {
            OrderDto orderDto = new OrderDto();
            BeanUtils.copyProperties(item, orderDto);

            LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderDetail::getOrderId, item.getId());
            List<OrderDetail> orderDetails = orderDetailService.list(wrapper);

            orderDto.setOrderDetails(orderDetails);

            return orderDto;
        }).collect(Collectors.toList());

        orderDtoPage.setRecords(list);

        return R.success(orderDtoPage);
    }

    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders, HttpSession session) {
        log.info("再来一单：{}", orders);

        //根据订单号 查询订单明细表
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(orders.getId() != null, OrderDetail::getOrderId, orders.getId());
        List<OrderDetail> orderDetails = orderDetailService.list(queryWrapper);

        List<ShoppingCart> shoppingCartList = orderDetails.stream().map((item) -> {
            ShoppingCart shoppingCart = new ShoppingCart();

            //将订单明细表的数据添加到购物车里
            shoppingCart.setUserId((Long) session.getAttribute("user"));
            shoppingCart.setName(item.getName());
            shoppingCart.setImage(item.getImage());
            shoppingCart.setDishId(item.getDishId());
            shoppingCart.setSetmealId(item.getSetmealId());
            shoppingCart.setDishFlavor(item.getDishFlavor());
            shoppingCart.setNumber(item.getNumber());
            shoppingCart.setAmount(item.getAmount());

            return shoppingCart;
        }).collect(Collectors.toList());

        boolean flag = shoppingCartService.saveBatch(shoppingCartList);

        orderService.removeById(orders.getId());
        orderDetailService.remove(queryWrapper);

        return flag == true ? R.success("再次下单成功") : R.error("再次下单失败");
    }

}
